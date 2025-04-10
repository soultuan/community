package com.tuanzisama.community.controller;

import com.google.code.kaptcha.Producer;
import com.tuanzisama.community.config.KaptchaConfig;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.RedisKeyUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private KaptchaConfig kaptchaConfig;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

    @GetMapping("/kaptcha")
    public void kaptcha(HttpServletResponse response) throws IOException {
        Producer producer = kaptchaConfig.kaptchaProducer();
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        //验证码存入redis
        String kaptchaUUID = CommunityUtil.generateUUID();
        String redisKey_ownerKaptcha = RedisKeyUtil.getKaptchaKey(kaptchaUUID);
        stringRedisTemplate.opsForValue().set(redisKey_ownerKaptcha,text,60, TimeUnit.SECONDS);

        //携带临时凭证的cookie信息
        Cookie kaptchaOwner = new Cookie("kaptchaOwner", kaptchaUUID);
        kaptchaOwner.setPath(contextPath);
        kaptchaOwner.setMaxAge(60);
        response.addCookie(kaptchaOwner);

        response.setContentType("image/png");
        try {
            ImageIO.write(image, "png", response.getOutputStream());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public String login(String username, String password, String code, @RequestParam(value = "rememberme",defaultValue = "false") Boolean rememberme, Model model,@CookieValue("kaptchaOwner") String kaptchaUUID, HttpServletResponse response) throws IOException {
        String kaptcha=null;
        if(!StringUtils.isEmpty(kaptchaUUID)){
            String redisKey_ownerKaptcha = RedisKeyUtil.getKaptchaKey(kaptchaUUID);
            kaptcha = stringRedisTemplate.opsForValue().get(redisKey_ownerKaptcha);
        }
        if(StringUtils.isEmpty(code) ||StringUtils.isEmpty(kaptcha)||!kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }

        Map<String, Object> map = userService.login(username, password, rememberme);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            Integer expiredTime = rememberme?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
            cookie.setMaxAge(expiredTime);
            response.addCookie(cookie);
            return "redirect:/index";
        }else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    @GetMapping("/logout")
    private String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }
}
