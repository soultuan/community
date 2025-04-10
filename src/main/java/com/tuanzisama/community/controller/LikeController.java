package com.tuanzisama.community.controller;

import com.tuanzisama.community.event.EventProducer;
import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.pojo.Event;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.CommentService;
import com.tuanzisama.community.service.DiscussPostService;
import com.tuanzisama.community.service.LikeService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {
    @Autowired
    private LikeService likeService;
    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId,int entityUserId,int postId) {
        User user = ThreadLocalUtil.get();
        likeService.like(user.getId(),entityType,entityId, entityUserId);
        Long likeNum = likeService.countLike(entityType, entityId);
        Integer likeStatus = likeService.likeStatus(user.getId(), entityType, entityId);
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("likeCount",likeNum);
        resultMap.put("likeStatus",likeStatus);

        if(likeStatus==1){
            Event event = new Event()
                    .setTopic(EVENT_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setUserId(user.getId())
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJsonString(0,null,resultMap);
    }
}
