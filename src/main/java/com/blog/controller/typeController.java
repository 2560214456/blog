package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.TBlog;
import com.blog.entity.TType;
import com.blog.vo.blogUserVo;
import com.blog.vo.typeAndCountBlog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class typeController extends BaseEntity {

    @GetMapping("/types/{id}")
    public String allType(@PathVariable("id") Long id, Model model){
        //查询所有的分类信息和每个分类信息的博客条数
        List<typeAndCountBlog> types = typeService.allType(100000);
        if (id == 1L)
            id = types.get(0).getId();
        model.addAttribute("types",types);
        model.addAttribute("id",id);
        //根据分类id查询博客信息
        IPage<blogUserVo> pageByBlog = blogService.findPageByBlog(id, getPage());
        model.addAttribute("page",blogService.findPageByBlog(id,getPage()));
        model.addAttribute("typeSize",types.size());
        return "types";
    }
}
