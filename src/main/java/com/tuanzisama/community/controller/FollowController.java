package com.tuanzisama.community.controller;

import com.tuanzisama.community.event.EventProducer;
import com.tuanzisama.community.pojo.Event;
import com.tuanzisama.community.pojo.Page;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.FollowService;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    private FollowService followService;
    @Autowired
    private UserService userService;
    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = ThreadLocalUtil.get();
        followService.follow(user.getId(),entityType,entityId);

        Event event = new Event()
                .setTopic(EVENT_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId)
                .setUserId(user.getId());
        eventProducer.fireEvent(event);
        return CommunityUtil.getJsonString(0,"关注成功！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = ThreadLocalUtil.get();
        followService.unfollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJsonString(0,"取消关注！");
    }

    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.selectUserById(userId);
        if(user==null){
            throw new RuntimeException("用户不存在！");
        }
        model.addAttribute("user",user);

        page.setPath("/followees/" + userId);
        page.setLimit(5);
        page.setRows(followService.followeeCount(userId,ENTITY_TYPE_USER).intValue());

        List<Map<String, Object>> maps = followService.selectFolloweeByUserId(userId, page.getOffset(), page.getLimit());
        if(maps!=null){
            for (Map<String, Object> map : maps) {
                User inUser = (User) map.get("user");
                boolean flag = hasFollowed(ENTITY_TYPE_USER, inUser.getId());
                map.put("hasFollowed", flag);
            }
        }
        model.addAttribute("users", maps);
        return "/site/followee";
    }

    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Model model, Page page) {
        User user = userService.selectUserById(userId);
        if(user==null){
            throw new RuntimeException("用户不存在！");
        }
        model.addAttribute("user",user);

        page.setPath("/followers/" + userId);
        page.setLimit(5);
        page.setRows(followService.followerCount(userId,ENTITY_TYPE_USER).intValue());

        List<Map<String, Object>> maps = followService.selectFollowerByUserId(userId, page.getOffset(), page.getLimit());
        if(maps!=null){
            for (Map<String, Object> map : maps) {
                User inUser = (User) map.get("user");
                boolean flag = hasFollowed(ENTITY_TYPE_USER, inUser.getId());
                map.put("hasFollowed", flag);
            }
        }
        model.addAttribute("users", maps);
        return "/site/follower";
    }

    private boolean hasFollowed(int entityType,int entityId) {
        User user = ThreadLocalUtil.get();
        if(user==null){
            return false;
        }
        return followService.isFollower(user.getId(),entityType,entityId);
    }
}
