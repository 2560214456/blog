package com.blog.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.TBlog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.TTag;
import com.blog.search.model.blogDocument;
import com.blog.vo.blogUserVo;
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
public interface TBlogMapper extends BaseMapper<TBlog> {
    //分页查询博客和发布者信息
    @Select("SELECT * FROM t_blog b LEFT JOIN t_user u ON b.`user_id` = u.id ${ew.customSqlSegment}")
    IPage<blogUserVo> findPageByBlog(Page page, @Param("ew") QueryWrapper<blogUserVo> wrapper);

    @Select("SELECT * FROM t_blog b LEFT JOIN t_user u ON b.user_id = u.id LEFT JOIN t_blog_tags bt ON b.id = bt.blogs_id ${ew.customSqlSegment}")
    IPage<blogUserVo> findTagIdByBlog(Page page,@Param("ew") QueryWrapper<blogUserVo> wrapper);

    @Select("SELECT t.* FROM t_blog b LEFT JOIN t_blog_tags bt ON b.`id` = bt.blogs_id LEFT JOIN t_tag t ON bt.tags_id = t.`id` WHERE b.`id` = #{id}")
    List<TTag> findBlogIdByTag(@Param("id") Long id);

    @Select("SELECT DATE_FORMAT(t_blog.`update_time`,'%Y')AS YEAR FROM t_blog WHERE published = TRUE GROUP BY DATE_FORMAT(t_blog.`update_time`,'%Y') ORDER BY YEAR DESC")
    List<String> findBlogByCreateTime();

    @Select("SELECT * FROM t_blog WHERE published = TRUE AND DATE_FORMAT(t_blog.`update_time`,'%Y') = ${createTime} order by create_time desc")
    List<TBlog> findByCreateTImeAllBLog(@Param("createTime") String createTime);

    @Select("SELECT * FROM t_blog b LEFT JOIN t_user u ON b.`user_id` = u.id WHERE b.`id` = #{id}")
    blogUserVo findByBLogId(@Param("id") Long id);
    //分页查询博客信息， 保存在es中
    IPage<blogDocument> findByAllAddES(Page page);
    //根据id 查询 返回blogDocument
    blogDocument getBlogDocumentBuId(@Param("id") Long id);
}
