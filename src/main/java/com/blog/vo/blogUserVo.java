package com.blog.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.blog.entity.TBlog;
import com.blog.entity.TTag;
import com.blog.entity.TUser;
import lombok.Data;

import java.util.List;

@Data
public class blogUserVo extends TBlog {
    //user
    /**
     * 头像
     */
    private String avatar;
    /**
     * 名称
     */
    private String nickname;
    /**
     * 博客使用的标签集合
     */
    List<TTag> tags;
}
