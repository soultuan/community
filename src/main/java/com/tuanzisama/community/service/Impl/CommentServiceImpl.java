package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.mapper.CommentMapper;
import com.tuanzisama.community.mapper.DiscussPostMapper;
import com.tuanzisama.community.pojo.Comment;
import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.service.CommentService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, Integer offset,Integer limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public Integer countCommentByEntity(Integer entityType, Integer entityId) {
        return commentMapper.countCommentByEntity(entityType, entityId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public void addComment(Comment comment) {
        if(comment==null){
            throw new IllegalArgumentException("参数为空！");
        }

        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.sensitiveFilt(comment.getContent()));
        Integer insertNums = commentMapper.insertComment(comment);

        if(comment.getEntityType()==ENTITY_TYPE_POST){
            Integer count = commentMapper.countCommentByEntity(comment.getEntityType(), comment.getEntityId());
            Integer updateNums = discussPostMapper.updateCommentCount(count,comment.getEntityId());
        }
    }
}
