package com.blog.entity;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TBlog extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 赞赏是否开启
     */
    private Boolean appreciation;

    /**
     * 评论是否开启
     */
    private Boolean commentabled;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 首图
     */
    private String firstPicture;

    /**
     * 标记
     */
    private String flag;

    /**
     * 博客状态，发布还是草稿
     */
    private Boolean published;

    /**
     * 是否推荐
     */
    private Boolean recommend;

    /**
     * 转载声明是否开启
     */
    private Boolean shareStatement;

    /**
     * 标题
     */
    private String title;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 浏览次数
     */
    private Integer views;

    /**
     * 外键，关联type表的id
     */
    private Long typeId;

    /**
     * 外键关联user表的id
     */
    private Long userId;

    /**
     * 博客描述
     */
    private String description;

    /**
     * 前端提交博客使用的标签id  格式是1,2,3,4....
     */
    @TableField(exist = false)
    private List<Long> tagIds;


}
