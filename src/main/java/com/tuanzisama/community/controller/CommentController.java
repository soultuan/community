package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.Comment;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.CommentService;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

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
        return "redirect:/discuss/detail/" + discussPostId;
    }
}
