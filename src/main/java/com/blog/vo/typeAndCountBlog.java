package com.blog.vo;

import com.blog.entity.TType;
import lombok.Data;

@Data
public class typeAndCountBlog extends TType {
    /**
     * 博客数量
     */
    private Integer blogSize;
}
