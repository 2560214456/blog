package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.vo.blogUserVo;
import com.blog.vo.tagAndCountBLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class tagController extends BaseEntity {
    @GetMapping("/tags/{id}")
    public String allTag(@PathVariable("id") Long id, Model model){
        //查询所有的标签和每个标签使用的博客数量
        List<tagAndCountBLog> tags =  tagService.findByPageTag(10000);
        if (id == 1)
            id = tags.get(0).getId();
        model.addAttribute("tags",tags);
        model.addAttribute("id",id);
        model.addAttribute("page",blogService.findTagIdByBlog(id,getPage()));
        model.addAttribute("countTag",tags.size());
        return "tags";
    }
}
