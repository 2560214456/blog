package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseEntity {
    @Autowired
    HttpServletRequest req;
    @Autowired
    ITBlogService blogService;
    @Autowired
    ITTagService tagService;
    @Autowired
    ITTypeService typeService;
    @Autowired
    ITCommentService commentService;
    @Autowired
    com.blog.service.searchService searchService;

    //获取分页对象
    public Page getPage(){
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 8);
        return new Page(pn,size);
    }
}
