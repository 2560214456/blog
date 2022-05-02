package com.blog.controller.admin;

import com.blog.entity.TUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class loginController {
    @GetMapping
    public String login(){
        return "admin/login";
    }
    //登录校验
    @PostMapping("/login")
    public String LoginVer(TUser user, HttpSession session, RedirectAttributes attributes){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(),user.getPassword());
        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
            attributes.addFlashAttribute("message","账号不存在");
            return "redirect:/admin";
        }catch (IncorrectCredentialsException e){
            attributes.addFlashAttribute("message","密码错误");
            return "redirect:/admin";
        }
        return "admin/index";
    }
    //退出登录状态
    @GetMapping("/logout")
    public String logout(HttpSession session){
        SecurityUtils.getSubject().logout();
        return "redirect:/admin";
    }
}
