package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.Comment;
import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.pojo.Page;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.CommentService;
import com.tuanzisama.community.service.DiscussPostService;
import com.tuanzisama.community.service.LikeService;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

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
    public String detail(@PathVariable("discussPostId") Integer discussPostId, Model model, Page page) {
        DiscussPost discussPost =  discussPostService.selectDiscussPostById(discussPostId);
        model.addAttribute("post", discussPost);
        User user = userService.selectUserById(Integer.valueOf(discussPost.getUserId()));
        model.addAttribute("user", user);
        Long likeCount = likeService.countLike(ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("likeCount", likeCount);
        User loginUser = ThreadLocalUtil.get();
        Integer likeStatus = loginUser!=null?likeService.likeStatus(loginUser.getId(), ENTITY_TYPE_POST, discussPost.getId()):0;
        model.addAttribute("likeStatus",likeStatus);

        page.setRows(commentService.countCommentByEntity(ENTITY_TYPE_POST,discussPost.getId()));
        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);

        List<Map<String,Object>> commentList = new ArrayList<>();
        List<Comment> comments = commentService.selectCommentByEntity(ENTITY_TYPE_POST, discussPost.getId(),page.getOffset(),page.getLimit());
        if(comments != null){
            for (Comment comment : comments) {
                Map<String,Object> map = new HashMap<>();
                map.put("comment", comment);
                User commentUser = userService.selectUserById(comment.getUserId());
                map.put("user", commentUser);
                likeCount = likeService.countLike(ENTITY_TYPE_COMMENT, comment.getId());
                map.put("likeCount", likeCount);
                likeStatus = loginUser!=null?likeService.likeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, comment.getId()):0;
                map.put("likeStatus",likeStatus);

                List<Comment> replys = commentService.selectCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(),0,Integer.MAX_VALUE);
                List<Map<String,Object>> replyList = new ArrayList<>();
                if (replys != null) {
                    for (Comment reply : replys) {
                        Map<String,Object> replyMap = new HashMap<>();
                        User fromUser = userService.selectUserById(reply.getUserId());
                        replyMap.put("reply", reply);
                        replyMap.put("user", fromUser);
                        likeCount = likeService.countLike(ENTITY_TYPE_COMMENT, reply.getId());
                        replyMap.put("likeCount", likeCount);
                        likeStatus = loginUser!=null?likeService.likeStatus(loginUser.getId(), ENTITY_TYPE_COMMENT, reply.getId()):0;
                        replyMap.put("likeStatus",likeStatus);

                        if(reply.getTargetId()==0){
                            replyMap.put("target", null);
                        }else {
                            User targetUser = userService.selectUserById(reply.getTargetId());
                            replyMap.put("target", targetUser);
                        }
                        replyList.add(replyMap);
                    }
                }
                map.put("replys", replyList);

                Integer replyCount = commentService.countCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                map.put("replyCount", replyCount);
                commentList.add(map);
            }
        }
        model.addAttribute("comments", commentList);

        return "/site/discuss-detail";
    }
}
