package com.blog.controller;

import com.blog.entity.TComment;
import com.blog.entity.TUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class commentController extends BaseEntity {
    @Value("${comment.avatar}")
    private String avatar;
    //保存用户评论
    @PostMapping("/comment")
    public String addComment(TComment comment, HttpSession session){
        TUser user = (TUser) session.getAttribute("user");
        if (user != null){
            //博客评论
            comment.setAdminComment(true);
            comment.setAvatar(user.getAvatar());
            comment.setEmail(user.getEmail());
        }else{
            //普通用户评论
            comment.setAdminComment(false);
            comment.setAvatar(avatar);
        }
        //保存评论
        commentService.save(comment);
        return "redirect:/allComment/"+comment.getBlogId();
    }

    //根据博客id查询评论信息
    @GetMapping("/allComment/{id}")
    public String allComment(@PathVariable("id")Long id, Model model){
        List<TComment> comments = commentService.findBLogIdByAllComment(id);
        model.addAttribute("comments",comments);
        return "blog :: comment-box";
    }
}
