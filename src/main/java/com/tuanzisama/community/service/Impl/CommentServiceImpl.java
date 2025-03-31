package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.mapper.CommentMapper;
import com.tuanzisama.community.pojo.Comment;
import com.tuanzisama.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> selectCommentByEntity(Integer entityType, Integer entityId, Integer offset,Integer limit) {
        return commentMapper.selectCommentByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public Integer countCommentByEntity(Integer entityType, Integer entityId) {
        return commentMapper.countCommentByEntity(entityType, entityId);
    }
}
