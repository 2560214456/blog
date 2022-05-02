package com.blog.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class indexController extends BaseEntity {

    @RequestMapping({"","/","index"})
    public String index(Model model){
        //分页查询博客信息
        //查询最多博客使用的6个分类名称
        //查询最多博客使用的10个标签名称
        //最新推荐（查询最新发布的6条博客）
        model.addAttribute("page",blogService.findPageByBlog(null,getPage()));
        model.addAttribute("types",typeService.fingPageByType(6));
        model.addAttribute("tags",tagService.findByPageTag(10));
        model.addAttribute("blogs",blogService.findListByBlogSize(6));
        return "index";
    }

    @RequestMapping("/footer/newblog")
    public String get(Model model){
        //查询最新发布的3个博客
        model.addAttribute("newblog",blogService.findListByBlogSize(3));
        return "_fragments :: newblogList";
    }
}
