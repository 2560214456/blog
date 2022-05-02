package com.blog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.service.*;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class adminBaseEntity {
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
    ITBlogTagsService blogTagsService;
    @Autowired
    com.blog.service.searchService searchService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    AmqpTemplate amqpTemplate;

    //获取分页对象
    public Page getPage(){
        int pn = ServletRequestUtils.getIntParameter(req, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(req, "size", 8);
        return new Page(pn,size);
    }
}
