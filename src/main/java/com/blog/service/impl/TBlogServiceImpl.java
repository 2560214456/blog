package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.config.rabbitmqConfig;
import com.blog.entity.TBlog;
import com.blog.entity.TBlogTags;
import com.blog.entity.TTag;
import com.blog.entity.TUser;
import com.blog.mapper.TBlogMapper;
import com.blog.mapper.TTagMapper;
import com.blog.rabbitMQ.rabbitMQMessage;
import com.blog.search.model.blogDocument;
import com.blog.service.ITBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.service.ITBlogTagsService;
import com.blog.service.ITTagService;
import com.blog.utli.MarkdownUtils;
import com.blog.vo.blogUserVo;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.ibatis.logging.jdbc.BaseJdbcLogger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
@Service
public class TBlogServiceImpl extends ServiceImpl<TBlogMapper, TBlog> implements ITBlogService {
    @Autowired
    TBlogMapper blogMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ITBlogTagsService blogTagsService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * 根据博客发布时间倒叙分页查询
     * @param page
     * @return
     */
    @Override
    public IPage<blogUserVo> findPageByBlog(Long typeid,Page page) {
        QueryWrapper<blogUserVo> wrapper = new QueryWrapper<>();
        wrapper.eq("published",true);
        wrapper.eq(typeid != null,"b.type_id",typeid);
        wrapper.orderByDesc("b.create_time");
        return blogMapper.findPageByBlog(page,wrapper);
    }

    /**
     * 查询最新发布的size条博客
     * @param size
     * @return
     */
    @Override
    public List<TBlog> findListByBlogSize(Integer size) {
        return blogMapper.selectList(new LambdaQueryWrapper<TBlog>()
                .orderByDesc(TBlog::getCreateTime)
                .last("limit "+size)
                .eq(TBlog::getPublished,true)
        );
    }

    //根据标签id查询博客信息
    @Override
    public IPage<blogUserVo> findTagIdByBlog(Long id, Page page) {
        QueryWrapper<blogUserVo> wrapper = new QueryWrapper<>();
        wrapper.eq("published",true);
        wrapper.eq("bt.tags_id",id);
        wrapper.orderByDesc("b.views");
        IPage<blogUserVo> tagIdByBlog = blogMapper.findTagIdByBlog(page, wrapper);
        tagIdByBlog.getRecords().forEach(blogUserVo -> {
             blogUserVo.setTags(blogMapper.findBlogIdByTag(blogUserVo.getId()));
        });
        return tagIdByBlog ;
    }

    /**
     * 查询发布的博客的年份
     * @return
     */
    @Override
    public List<String> findBlogByCreateTime() {
        return blogMapper.findBlogByCreateTime();
    }

    /**
     * 根据年份查询这年发布的全部博客
     * @param s 年份
     * @return
     */
    @Override
    public List<TBlog> findByCreateTImeAllBLog(String s) {
        return blogMapper.findByCreateTImeAllBLog(s);
    }

    /**
     * 根据博客id查询博客具体信息
     * @param id 博客id
     * @return
     */
    @Override
    public blogUserVo findById(Long id) {
        //根据id查询博客信息
        blogUserVo blogUserVo = blogMapper.findByBLogId(id);
        //根据博客id查询博客使用的标签信息
        blogUserVo.setTags(blogMapper.findBlogIdByTag(id));
        //把数据库中的markdown格式解析成html格式
        blogUserVo.setContent(MarkdownUtils.markdownToHtmlExtensions(blogUserVo.getContent()));
        return blogUserVo;
    }

    /**
     * 缓存博客的阅读量
     * @param blog
     */
    @Override
    public void cacheBLogViews(blogUserVo blog) {
        String key = "views";
        String key2 = "blogId:"+blog.getId();
        //再缓存中获取博客的浏览量
        Object view = redisTemplate.opsForHash().get(key, key2);
        if (view == null){
            //缓存中没有该博客的浏览量，需要把当前博客的浏览量存储在redis中
            blog.setViews(blog.getViews()+1);
            redisTemplate.opsForHash().put(key,key2,blog.getViews());
        }else{
            //缓存中已经有当前博客的浏览量缓存,把缓存中的浏览量加一
            blog.setViews((Integer) view+1);
            redisTemplate.opsForHash().increment(key,key2,1);
        }
    }

    /**
     * 添加或修改博客
     * @param blog
     * @return
     */
    @Override
    public String addOrUpdateBlog(TBlog blog) {
        if (blog.getId() == null){
            //添加
            //保存博客
            int insert = blogMapper.insert(blog);
            //保存博客使用的分类信息
            List<TBlogTags> list = new ArrayList<>();
            blog.getTagIds().forEach(tagid ->{
                list.add(new TBlogTags(blog.getId(),tagid));
            });
            boolean b = blogTagsService.saveBatch(list);
            if (insert > 0 && b)
                return "添加成功";
        }else{
            //修改
            int i = blogMapper.updateById(blog);
            //根据博客id删所有博客使用的标签
            blogTagsService.remove(new LambdaQueryWrapper<TBlogTags>().eq(TBlogTags::getBlogsId,blog.getId()));
            //在把修改之后的标签信息添加进去
            List<TBlogTags> list = new ArrayList<>();
            blog.getTagIds().forEach(tagid ->{
                list.add(new TBlogTags(blog.getId(),tagid));
            });
            boolean b = blogTagsService.saveBatch(list);
            if (i > 0 && b){
                return "修改成功";
            }
        }
        //通知es  修改博客信息
        rabbitTemplate.convertAndSend(rabbitmqConfig.BLOG_EXCHANGE_NAME,rabbitmqConfig.ROUTINGKEY_KEY
                ,new rabbitMQMessage(blog.getId(),rabbitMQMessage.CREATE_UPDATE));
        return "添加失败";
    }

    /**
     * 分页查询blog信息，保存在es中
     * @param page
     * @return
     */
    @Override
    public IPage<blogDocument> findByAllAddES(Page page) {

        return blogMapper.findByAllAddES(page);
    }

    @Override
    public blogDocument getBlogDocumentBuId(Long id) {

        return blogMapper.getBlogDocumentBuId(id);
    }
}
