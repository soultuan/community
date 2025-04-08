package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.LikeService;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId,int entityUserId ) {
        User user = ThreadLocalUtil.get();
        likeService.like(user.getId(),entityType,entityId, entityUserId);
        Long likeNum = likeService.countLike(entityType, entityId);
        Integer likeStatus = likeService.likeStatus(user.getId(), entityType, entityId);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("likeCount",likeNum);
        resultMap.put("likeStatus",likeStatus);
        return CommunityUtil.getJsonString(0,null,resultMap);
    }
}
