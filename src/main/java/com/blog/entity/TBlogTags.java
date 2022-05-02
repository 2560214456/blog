package com.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2022-04-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@AllArgsConstructor
public class TBlogTags extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 外键关联blog表的id
     */
    private Long blogsId;

    /**
     * 外键关联tag表的id
     */
    private Long tagsId;


}
