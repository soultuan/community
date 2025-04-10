package com.tuanzisama.community.controller;

import com.tuanzisama.community.event.EventProducer;
import com.tuanzisama.community.pojo.Comment;
import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.pojo.Event;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.CommentService;
import com.tuanzisama.community.service.DiscussPostService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    private CommentService commentService;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private DiscussPostService discussPostService;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") Integer discussPostId, Comment comment) {
        User user = ThreadLocalUtil.get();
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        comment.setUserId(user.getId());
        if(comment.getTargetId()==null){
            comment.setTargetId(0);
        }
        commentService.addComment(comment);

        Event event = new Event();
        event.setTopic(EVENT_COMMENT)
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setUserId(user.getId())
                .setData("postId", discussPostId);
        if(comment.getEntityType()==ENTITY_TYPE_COMMENT){
            Comment selected = commentService.selectCommentById(comment.getEntityId());
            event.setEntityUserId(selected.getUserId());
        }else if(comment.getEntityType()==ENTITY_TYPE_POST){
            DiscussPost discussPost = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(Integer.parseInt(discussPost.getUserId()));
        }
        eventProducer.fireEvent(event);
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
