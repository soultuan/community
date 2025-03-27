package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getloginPage() {
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model,User user) {
        Map<String, Object> msgMap = userService.register(user);
        if(msgMap.isEmpty()||msgMap==null){
            model.addAttribute("msg","注册成功，请查收您的邮箱进行账号激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",msgMap.get("usernameMsg"));
            model.addAttribute("emailMsg",msgMap.get("emailMsg"));
            model.addAttribute("passwordMsg",msgMap.get("passwordMsg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{activationCode}")
    public String activation(Model model, @PathVariable("userId") Integer userId,@PathVariable("activationCode") String activationCode) {
        Integer result = userService.activation(userId,activationCode);
        if(result==ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功！");
            model.addAttribute("target","/login");
        } else if (result==ACTIVATION_FAILURE) {
            model.addAttribute("msg","激活失败！");
            model.addAttribute("target","/index");
        }else {
            model.addAttribute("msg","请勿重复激活！");
            model.addAttribute("target","/index");
        }

        return "/site/operate-result";
    }
}
