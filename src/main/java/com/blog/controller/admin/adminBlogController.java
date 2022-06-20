package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.config.rabbitmqConfig;
import com.blog.entity.*;
import com.blog.mapper.TBlogTagsMapper;
import com.blog.rabbitMQ.rabbitMQMessage;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/admin")
@Controller
public class adminBlogController extends adminBaseEntity {
    @GetMapping("/blogs")
    public String  blogs(Model model){
        LambdaQueryWrapper<TBlog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TBlog::getCreateTime);
        model.addAttribute("page",blogService.page(getPage(),wrapper));
        model.addAttribute("types",typeService.list());
        return "admin/blogs";
    }
    //查询
    @RequestMapping("/blogs/query")
    public String queryBlog(ConditionQuery query,Model model){
        LambdaQueryWrapper<TBlog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(query.getTitle() != null,TBlog::getTitle,query.getTitle());
        wrapper.eq(query.getTypeId()!=null,TBlog::getTypeId,query.getTypeId());
        wrapper.eq(query.isRecommend(),TBlog::getRecommend,true);
        wrapper.orderByDesc(TBlog::getCreateTime);
        model.addAttribute("page",blogService.page(getPage(),wrapper));
        model.addAttribute("types",typeService.list());
        return "admin/blogs :: blogList";
    }
    //跳转新增博客页面
    @GetMapping("/blogs/insertBlog")
    public String jumpAddBlog(Model model){
        model.addAttribute("blog",new TBlog());
        model.addAttribute("types",typeService.list());
        model.addAttribute("tags",tagService.list());
        return "admin/blogs-input";
    }
    //跳转修改博客页面
    @GetMapping("/blogs/{id}/updateBlog")
    public String updateBlog(@PathVariable("id")Long id,Model model){
        model.addAttribute("blog",blogService.getById(id));
        model.addAttribute("types",typeService.list());
        model.addAttribute("tags",tagService.list());
        List<TBlogTags> list = blogTagsService.list(new LambdaQueryWrapper<TBlogTags>().eq(TBlogTags::getBlogsId, id));
        StringBuilder blogTags = new StringBuilder();
        blogTags.append(list.get(0).getTagsId());
        for (int i = 1; i < list.size(); i++) {
            blogTags.append(","+list.get(i).getTagsId());
        }
        model.addAttribute("tag",blogTags.toString());
        return "admin/blogs-input";
    }
    //新增或修改博客保存
    @PostMapping("/blogs/addBlog")
    public String addOrUpdateBlog(TBlog blog, RedirectAttributes attributes, HttpSession session){
        TUser user = (TUser) session.getAttribute("user");
        blog.setUserId(user.getId());
        attributes.addFlashAttribute("message",blogService.addOrUpdateBlog(blog));
        //RabbitMQ发送消息，修改或者添加博客
        /*rabbitTemplate.convertAndSend(rabbitmqConfig.BLOG_EXCHANGE_NAME,rabbitmqConfig.ROUTINGKEY_KEY
                ,new rabbitMQMessage(blog.getId(),rabbitMQMessage.CREATE_UPDATE));*/
        amqpTemplate.convertAndSend(rabbitmqConfig.BLOG_EXCHANGE_NAME,rabbitmqConfig.ROUTINGKEY_KEY
                ,new rabbitMQMessage(blog.getId(),rabbitMQMessage.CREATE_UPDATE));
        return "redirect:/admin/blogs";
    }
    //删除博客
    @RequestMapping("/blogs/{id}/deleteBlog/{pn}")
    public String deleteBlog(@PathVariable("id")Long id,@PathVariable("pn")Integer pn,Model model) throws IOException {
        //删除es中的博客信息
        searchService.deleteById(id);
        //根据id删除博客
        boolean falg = blogService.removeById(id);
        //根据博客删除，删除博客引用的标签
        boolean b = blogTagsService.remove(new LambdaQueryWrapper<TBlogTags>().eq(TBlogTags::getBlogsId, id));
        //根据博客id删除博客下的所有评论
        commentService.remove(new LambdaQueryWrapper<TComment>().eq(TComment::getBlogId,id));
        //RabbitMQ发送消息，删除es中的博客数据
        /*rabbitTemplate.convertAndSend(rabbitmqConfig.BLOG_EXCHANGE_NAME,rabbitmqConfig.ROUTINGKEY_KEY
                ,new rabbitMQMessage(id,rabbitMQMessage.REMOVE));*/
        amqpTemplate.convertAndSend(rabbitmqConfig.BLOG_EXCHANGE_NAME,rabbitmqConfig.ROUTINGKEY_KEY
                ,new rabbitMQMessage(id,rabbitMQMessage.REMOVE));
        if (falg && b)
            model.addAttribute("message","删除成功");
        return "redirect:/admin/blogs";
    }
    //图片保存七牛云 并进行回显
    //上传图片并显示回显
    @ResponseBody
    @RequestMapping("/uploadImg")
    public Map<String,Object> uploadImg(HttpServletRequest request,
                                        @RequestParam(value = "editormd-image-file", required = false) MultipartFile file){
        Map<String,Object> map = new HashMap<>();

        if (file != null){
            //获取此项目的tomcat路径
            String webapp = request.getServletContext().getRealPath("/");
            try{
                //获取文件名
                String filename = file.getOriginalFilename();
                // 图片的在服务器上面的物理路径
                //   File destFile = new File(webapp, fileName);
                /*七牛云*/
                InputStream inputStream = file.getInputStream(); // 把图片转换为输出流
                byte[] bytes = toByteArray(inputStream); // 把图片输出流转换为字节数组
                test(bytes,priUuid+"/"+filename);//上传图片
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return map;
    }
    //把图片转换为byle字节数组
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    //上传图片
    public void test(byte[] bytes,String fileName){
        //构造一个带指定 Region 对象的配置类
        //Region.huanan() 连接的存储区域 我的七牛云是华南所以连接的是华南的
        Configuration cfg = new Configuration(Region.huanan());

    //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
    //...生成上传凭证，然后准备上传
    // 用户中心  密钥管理
        String accessKey = "uiJksmBVDjW6Ia5Idv5PBZcybQsmBKRoJWFaISd6";
        String secretKey = "5SI16tmH9kXMlvZ9B1jdbDBgQROBAEFpJcQqRqe2"; //
        String bucket = "imageimg"; //空间名称

    //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = fileName;

        try {
         /*   File file = new File("D:\\360downloads\\wpcache\\1.jpg");
            FileInputStream fileInputStream = new FileInputStream(file);*/
            //    byte[] bytes = this.toByteArray(fileInputStream);


            //    byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(bytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception e){
            e.getMessage();
        }

    }
}
