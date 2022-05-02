package com.blog.controller;

import com.blog.entity.TBlog;
import com.blog.vo.blogUserVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class blogShowController extends BaseEntity {
    @GetMapping("/blog/{id}")
    public String blog(@PathVariable("id")Long id, Model model){
        blogUserVo blog = blogService.findById(id);

        Assert.notNull(blog,"博客不存在");

        //缓存博客的浏览量
        blogService.cacheBLogViews(blog);
        model.addAttribute("blog",blog);
        return "blog";
    }
}
