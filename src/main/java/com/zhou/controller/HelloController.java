package com.zhou.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;

@Controller
public class HelloController{
    @GetMapping("/login")
    public String Test(){
        return "login";
    }

    @GetMapping({"/index","/"})
    public String getIndex(){
        System.out.println("访问首页");
        return "index";
    }
    @RequestMapping("/user/login")
    public String check(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session){
        if(!StringUtils.isEmpty(username) && "root".equals(password)){
            session.setAttribute("loginUser",username);
            return "redirect:/main.html";
        }
        else{
            model.addAttribute("msg","密码错误");
            return "index";
        }
    }


}