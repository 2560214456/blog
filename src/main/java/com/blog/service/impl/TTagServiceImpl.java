package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.blog.entity.TTag;
import com.blog.mapper.TTagMapper;
import com.blog.service.ITTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.vo.tagAndCountBLog;
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
public class TTagServiceImpl extends ServiceImpl<TTagMapper, TTag> implements ITTagService {
    @Autowired
    TTagMapper tagMapper;

    /**
     * 查询最多博客使用的10个标签名称
     * @param size
     * @return
     */
    @Override
    public List<tagAndCountBLog> findByPageTag(Integer size) {
        List<tagAndCountBLog> tTags = tagMapper.findByTagSize(new QueryWrapper<TTag>()
                .orderByDesc("blogSize")
                .groupBy("t.id")
                .last("LIMIT " + size)
        );
        return tTags;
    }
}
