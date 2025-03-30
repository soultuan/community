package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.DiscussPostService;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @PostMapping("/insertDiscussPost")
    @ResponseBody
    public String insertDiscussPost(String title, String content){
        User user = ThreadLocalUtil.get();
        if(user == null){
            return CommunityUtil.getJsonString(403,"用户未登录！");
        }

        discussPostService.insertDiscussPost(title,content);
        return CommunityUtil.getJsonString(0,"发布成功");
    }

    @GetMapping("/detail/{discussPostId}")
    public String detail(@PathVariable("discussPostId") Integer discussPostId, Model model){
        DiscussPost discussPost =  discussPostService.selectDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        User user = userService.selectUserById(Integer.valueOf(discussPost.getUserId()));
        model.addAttribute("user", user);
        return "/site/discuss-detail";
    }
}
