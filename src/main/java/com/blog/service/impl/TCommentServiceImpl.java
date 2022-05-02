package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.TComment;
import com.blog.mapper.TCommentMapper;
import com.blog.service.ITCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class TCommentServiceImpl extends ServiceImpl<TCommentMapper, TComment> implements ITCommentService {
    @Autowired
    TCommentMapper commentMapper;
    @Override
    public List<TComment> findBLogIdByAllComment(Long id) {
        LambdaQueryWrapper<TComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TComment::getBlogId,id);
        //根据博客id查询评论信息
        return eachComment(commentMapper.selectList(wrapper));
    }
    //找出顶级的评论
    public List<TComment> eachComment(List<TComment> comments){
        List<TComment> commentView = new ArrayList<>();
        comments.forEach(comment -> {
            if (comment.getParentCommentId() == null)
                commentView.add(comment);
        });
        //删除全部评论信息中的父评论
        comments.removeAll(commentView);
        return findSubComment(commentView, comments);
    }
    //临时存放子评论
    List<TComment> commentsPar = new ArrayList<>();
    /**
     * 找出父评论中的下一级的子评论
     * @param commentView 父评论
     * @param comments 全部评论
     * @return
     */
    public List<TComment> findSubComment(List<TComment> commentView,List<TComment> comments){
        commentView.forEach(tComment -> {
            comments.forEach(Comment->{
                if (Comment.getParentCommentId().equals(tComment.getId())){
                    //把父评论存放在子评论对象中
                    Comment.setSubComment(tComment);
                    commentsPar.add(Comment);
                    //调用方法在查询子评论的下一下评论信息，知道查询到没有为止
                    recursively(Comment,comments);
                }
            });
            tComment.setParentComment(commentsPar);//把子评论集合复制给顶级夫评论中的子评论集合
            comments.removeAll(commentsPar);
            commentsPar = new ArrayList<>();//清空子评论的临时存储区
        });
        return commentView;

    }

    public void recursively(TComment comment,List<TComment> comments){
        comments.forEach(comment1 -> {
            if (comment1.getParentCommentId().equals(comment.getId())){
                comment.setNextLevelComment(comment1);//添加下一级评论
                comment1.setSubComment(comment); //添加父评论
                commentsPar.add(comment1); //添加到临时的子评论存放区
                recursively(comment1,comments);//递归找出一个父评论下的所有子评论
            }
        });
    }
}
