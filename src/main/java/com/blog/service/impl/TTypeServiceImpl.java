package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.blog.entity.TType;
import com.blog.mapper.TTypeMapper;
import com.blog.service.ITTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.vo.typeAndCountBlog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
@Service
public class TTypeServiceImpl extends ServiceImpl<TTypeMapper, TType> implements ITTypeService {
    @Autowired
    TTypeMapper typeMapper;

    /**
     * 查询最新博客使用的6个分类名称
     * @param size
     * @return
     */
    @Override
    public List<typeAndCountBlog> fingPageByType(Integer size) {
        QueryWrapper<TType> wrapper = new QueryWrapper<>();
        wrapper.groupBy("t_blog.type_id");
        wrapper.orderByDesc("blogSize");
        //查询条数
        wrapper.last("limit "+size);
        return typeMapper.findSizeByType(wrapper);
    }

    /**
     * 查询所有分类信息
     * @return
     */
    @Override
    public List<typeAndCountBlog> allType(Integer size) {
        QueryWrapper wrapper = new QueryWrapper<TType>();
        wrapper.groupBy("b.type_id");
        wrapper.orderByDesc("blogSize");
        wrapper.last("limit "+size);
        return typeMapper.allType(wrapper);
    }
}
