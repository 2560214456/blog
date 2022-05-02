package com.blog.service;

import com.blog.entity.TTag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.vo.tagAndCountBLog;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2022-04-18
 */
public interface ITTagService extends IService<TTag> {

    List<tagAndCountBLog> findByPageTag(Integer size);
}
