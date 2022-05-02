package com.blog.vo;

import com.blog.entity.TTag;
import lombok.Data;

@Data
public class tagAndCountBLog extends TTag {
    /**
     * 博客数量
     */
    private Integer blogSize;
}
