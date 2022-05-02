package com.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.entity.TType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.vo.typeAndCountBlog;
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
public interface TTypeMapper extends BaseMapper<TType> {

    @Select("SELECT count(*) blogSize,t_type.* from t_type LEFT JOIN t_blog ON t_type.id = t_blog.type_id ${ew.customSqlSegment}")
    List<typeAndCountBlog> findSizeByType(@Param("ew") QueryWrapper<TType> wrapper);

    @Select("SELECT COUNT(*) AS blogSize,t.* FROM t_type t LEFT JOIN t_blog b ON t.id = b.type_id ${ew.customSqlSegment}")
    List<typeAndCountBlog> allType(@Param("ew") QueryWrapper wrapper);
}
