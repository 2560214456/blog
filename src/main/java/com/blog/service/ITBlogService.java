package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.TBlog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.search.model.blogDocument;
import com.blog.vo.blogUserVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
public interface ITBlogService extends IService<TBlog> {
    //分页查询博客
    IPage<blogUserVo> findPageByBlog(Long id,Page page);
    //查询最新发布到的size条博客
    List<TBlog> findListByBlogSize(Integer size);
    //根据标签id查询博客信息
    IPage<blogUserVo> findTagIdByBlog(Long id, Page page);
    //查询博客的发布年分
    List<String> findBlogByCreateTime();
    //根据年份查询这年发布的全部博客
    List<TBlog> findByCreateTImeAllBLog(String s);
    //根据博客id博客的具体信息
    blogUserVo findById(Long id);
    //缓存博客的阅读量
    void cacheBLogViews(blogUserVo blog);

    //添加或修改博客
    String addOrUpdateBlog(TBlog blog);
    //分页查询博客信息，保存到es中
    IPage<blogDocument> findByAllAddES(Page page);

    //es 根据id查询
    blogDocument getBlogDocumentBuId(Long id);
}
