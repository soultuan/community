package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.FollowService;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = ThreadLocalUtil.get();
        followService.follow(user.getId(),entityType,entityId);

        return CommunityUtil.getJsonString(0,"关注成功！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = ThreadLocalUtil.get();
        followService.unfollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJsonString(0,"取消关注！");
    }
}
