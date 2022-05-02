package com.blog.service;

import com.blog.entity.TComment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
public interface ITCommentService extends IService<TComment> {
    //根据博客id查询评论信息
    List<TComment> findBLogIdByAllComment(Long id);
}
