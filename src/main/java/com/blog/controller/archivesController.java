package com.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class archivesController extends BaseEntity {
    @GetMapping("/archives")
    public String archives(Model model){
        //查询发布博客的年份  ，2020，2021.....
        List<String> blogCreateTIme = blogService.findBlogByCreateTime();
        Map<String,Object> map = new HashMap<>();
        blogCreateTIme.forEach(s -> {
            //根据年份，查询全部博客
            map.put(s,blogService.findByCreateTImeAllBLog(s));
        });
        model.addAttribute("archives",map);
        model.addAttribute("countBLog",blogService.count());
        return "archives";
    }
}
