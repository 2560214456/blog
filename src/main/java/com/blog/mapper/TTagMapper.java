package com.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.entity.TTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.vo.tagAndCountBLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
public interface TTagMapper extends BaseMapper<TTag> {
    @Select(" SELECT COUNT(*) AS blogSize,t.* FROM t_tag t LEFT JOIN t_blog_tags bt ON t.`id` = bt.tags_id ${ew.customSqlSegment}")
    List<tagAndCountBLog> findByTagSize(@Param("ew") QueryWrapper<TTag> wrapper);
}
