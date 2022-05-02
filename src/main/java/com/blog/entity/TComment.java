package com.blog.entity;

import java.time.LocalDateTime;
import java.util.List;

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
public class TComment extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 评论的内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 名称
     */
    private String nickname;

    /**
     * 外键，关联的blog表的id
     */
    private Long blogId;

    /**
     * 外键，关联comment评论表表的id
     */
    private Long parentCommentId;

    /**
     * 判断是否是博主发表的评论
     */
    private Boolean adminComment;

    /**
     * 父评论，没有就表示时顶级评论
     */
    @TableField(exist = false)
    private TComment subComment;
    /**
     *  子评论集合
     */
    @TableField(exist = false)
    private List<TComment> parentComment;

    /**
     * 下一级评论
     */
    @TableField(exist = false)
    private TComment nextLevelComment;


}
