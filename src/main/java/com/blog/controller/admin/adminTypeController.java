package com.blog.controller.admin;

import com.blog.entity.TType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/admin")
@Controller
public class adminTypeController extends adminBaseEntity {

    //分页查询所有标签
    @GetMapping("/types")
    public String allType(Model model){
        model.addAttribute("typePage",typeService.page(getPage()));
        return "admin/types";
    }
    //跳转新增标签页面
    @GetMapping("/types/insertType")
    public String insertType(Model model){
        model.addAttribute("type",new TType());
        return "admin/types-input";
    }
    //保存标签type
    @PostMapping("/types/insertType")
    public String addType(TType type, RedirectAttributes attributes){
        boolean save = typeService.save(type);
        if (save)
            attributes.addFlashAttribute("message","保存成功");
        return "redirect:/admin/types";
    }
    //跳转修改标签页面
    @GetMapping("/types/{id}/UpdateType")
    public String updateType(@PathVariable("id")Long id,Model model){
        model.addAttribute("type",typeService.getById(id));
        return "admin/types-input";
    }
    //修改标签保存数据
    @PostMapping("/type/updateType")
    public String updateType(TType type,RedirectAttributes attributes){
        if (typeService.updateById(type))
            attributes.addFlashAttribute("message","修改成功");
        return "redirect:/admin/types";
    }
    //删除标签
    @GetMapping("/types/{id}/deleteType")
    public String deleteType(@PathVariable("id")Long id,RedirectAttributes attributes){
        if (typeService.removeById(id))
            attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/types";
    }
}
