package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.search.model.blogDocument;
import com.blog.service.searchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class queryController extends BaseEntity {


    @GetMapping("/search")
    public String search(String query, Model model){
        IPage<blogDocument> search = searchService.search(getPage(), query);
        model.addAttribute("page",search);
        return "search";
    }
}
