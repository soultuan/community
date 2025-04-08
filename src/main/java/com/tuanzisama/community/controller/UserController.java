package com.tuanzisama.community.controller;

import com.tuanzisama.community.annotation.LoginRequired;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.LikeService;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;
    @Value("${community.path.upload}")
    private String uploadPath;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @LoginRequired
    @GetMapping("/setting")
    public String setting() {
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeaderUrl(@RequestBody MultipartFile headerImage, Model model) {
        String fileName = headerImage.getOriginalFilename();
        if(headerImage==null) {
            model.addAttribute("error","文件未上传!");
            return "/site/setting";
        }

        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isEmpty(suffix)) {
            model.addAttribute("error","文件格式错误!");
            return "/site/setting";
        }

        fileName = CommunityUtil.generateUUID() + suffix;
        String storeFileName = uploadPath + "/" + fileName;
        try {
            headerImage.transferTo(new File(storeFileName));
        } catch (IOException e) {
            logger.error("文件上传失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！",e);
        }

        User user = ThreadLocalUtil.get();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/header/{headerImage}")
    public void getHeader(@PathVariable("headerImage") String headerImage,HttpServletResponse response) {
        String suffix = headerImage.substring(headerImage.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        File file = new File(uploadPath + "/" + headerImage);
        try(FileInputStream fis = new FileInputStream(file);
            ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b=fis.read(buffer))!=-1) {
                outputStream.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("文件读取失败："+e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword,@CookieValue("ticket") String ticket,Model model) {
        if (StringUtils.isEmpty(oldPassword)) {
            model.addAttribute("oldError","密码不能为空!");
            return "/site/setting";
        }

        User user = ThreadLocalUtil.get();
        if(!CommunityUtil.md5(oldPassword+user.getSalt()).equals(user.getPassword())){
            model.addAttribute("oldError","密码错误!");
            return "/site/setting";
        }

        if(StringUtils.isEmpty(newPassword)){
            model.addAttribute("newError","密码不能为空!");
            return "/site/setting";
        }

        userService.updatePassword(user.getId(),CommunityUtil.md5(newPassword+user.getSalt()));
        userService.logout(ticket);
        return "redirect:/login";
    }

    @GetMapping("/profile/{userId}")
    public String profile(@PathVariable("userId") Integer userId, Model model) {
        User user = userService.selectUserById(userId);
        if(user==null) {
            throw new IllegalArgumentException("用户不存在！");
        }
        Integer likeCount = likeService.countUserLike(userId);
        model.addAttribute("user",user);
        model.addAttribute("likeCount",likeCount);

        return "/site/profile";
    }

}
