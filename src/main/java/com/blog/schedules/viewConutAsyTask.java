package com.blog.schedules;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.TBlog;
import com.blog.service.ITBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//定时器
@Component
public class viewConutAsyTask {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ITBlogService blogService;
    @Scheduled(cron = "0 0/1 * * * *")//每一分钟同步一次
    public void task(){
        //获取redis中指定hash类型的所有键值
        Map<String,Object> views = redisTemplate.opsForHash().entries("views");
        List ids = new ArrayList();
        views.keySet().forEach(s -> {
            ids.add(s.substring("blogId:".length()));
        });
        if (ids.isEmpty()) return;

        List<TBlog> blogs = blogService.list(new LambdaQueryWrapper<TBlog>().in(TBlog::getId, ids));

        blogs.forEach(tBlog -> {
            tBlog.setViews((Integer) redisTemplate.opsForHash().get("views","blogId:"+tBlog.getId()));
        });

        boolean b = blogService.updateBatchById(blogs);
        if (b){
            ids.forEach(id->{
                redisTemplate.opsForHash().delete("views","blogId:"+id);
                System.out.println("同步成功");
            });
        }
    }
}
