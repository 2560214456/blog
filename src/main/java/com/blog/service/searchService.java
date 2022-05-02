package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.rabbitMQ.rabbitMQMessage;
import com.blog.search.model.blogDocument;

import java.io.IOException;
import java.util.List;

public interface searchService {
    //同步mysql中的blog信息到es中
    int initEsData(List<blogDocument> records);
    //分页查询es
    IPage<blogDocument> search(Page page, String s);

    //分页查询es ,高亮显示
    public IPage<blogDocument> search1(Page page, String keyword) throws IOException;
    //删除
    void deleteById(Long id) throws IOException;
    //修改或添加
    void sreateOrdateIndex(rabbitMQMessage message);
    //删除
    void removeIndex(rabbitMQMessage message);
}
