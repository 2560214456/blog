package com.blog.controller.admin;

import com.blog.entity.TTag;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@RequestMapping("/admin")
@Controller
public class adminTagController extends adminBaseEntity {
    //分页查询所有分类信息
    @GetMapping("/tags")
    public String allPageTag(Model model, HttpSession session){
        model.addAttribute("tags",tagService.page(getPage()));
        return "admin/tags";
    }
    //修改分类
    @GetMapping("/tags/update/{id}")
    public String updateTag(@PathVariable("id")Long id,Model model){
        model.addAttribute("tag",tagService.getById(id));
        return "admin/tags-input";
    }
    //删除分类
    @GetMapping("/tags/delete/{id}")
    public String deleteByIdTag(@PathVariable("id")Long id, RedirectAttributes attributes){
        if (tagService.removeById(id))
            attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/tags";
    }
    //跳转添加标签页面
    @GetMapping("/insert")
    public String jumpTag(Model model){
        model.addAttribute("tag",new TTag());
        return "admin/tags-input";
    }
    //保存修改的标签信息
    @PostMapping("/tags/update")
    public String updateTag(TTag tag,RedirectAttributes attributes){
        if (tagService.updateById(tag))
            attributes.addFlashAttribute("message","修改成功");
        return "redirect:/admin/tags";
    }
    //保存新增的标签
    @PostMapping("/addTag")
    public String addTag(TTag tag,RedirectAttributes attributes){
        if (tagService.save(tag))
            attributes.addFlashAttribute("message","添加成功");
        return "redirect:/admin/tags";
    }

}
