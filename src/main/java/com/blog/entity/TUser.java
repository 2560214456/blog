package com.blog.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
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
public class TUser extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 头像
     */
    private String avatar;

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
     * 密码
     */
    private String password;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 用户名
     */
    private String username;


}
