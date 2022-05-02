package com.blog.service;

import com.blog.entity.TType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.vo.typeAndCountBlog;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
public interface ITTypeService extends IService<TType> {
    //查询前6个最多博客的分类信息
    List<typeAndCountBlog> fingPageByType(Integer size);

    //查询所有分类信息
    List<typeAndCountBlog> allType(Integer size);
}
