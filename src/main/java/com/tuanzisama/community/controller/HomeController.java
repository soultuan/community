package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.pojo.Page;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.DiscussPostService;
import com.tuanzisama.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;


    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        page.setRows(discussPostService.countDiscussPosts(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.selectDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> discussPostList = new ArrayList<>();
        if(list!=null&& !list.isEmpty()){
            for(DiscussPost post:list){
                Map<String, Object> discussPostMap = new HashMap<>();
                User user = userService.selectUserById(Integer.valueOf(post.getUserId()));
                discussPostMap.put("post",post);
                discussPostMap.put("user",user);
                discussPostList.add(discussPostMap);
            }
        }
        model.addAttribute("discussPostList",discussPostList);
        return "/index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/site/500";
    }
}
