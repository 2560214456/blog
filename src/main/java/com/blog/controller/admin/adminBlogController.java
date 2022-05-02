package com.blog.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.config.rabbitmqConfig;
import com.blog.entity.*;
import com.blog.mapper.TBlogTagsMapper;
import com.blog.rabbitMQ.rabbitMQMessage;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

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
        amqpTemplate.convertAndSend(rabbitmqConfig.BLOG_EXCHANGE_NAME,rabbitmqConfig.ROUTINGKEY_KEY
                ,new rabbitMQMessage(id,rabbitMQMessage.REMOVE));
        if (falg && b)
            model.addAttribute("message","删除成功");
        return "redirect:/admin/blogs";
    }
}
