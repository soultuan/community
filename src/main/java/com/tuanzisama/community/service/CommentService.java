package com.tuanzisama.community.service;

import com.tuanzisama.community.pojo.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> selectCommentByEntity(Integer entityType,Integer entityId,Integer offset,Integer limit);
    Integer countCommentByEntity(Integer entityType,Integer entityId);
    void addComment(Comment comment);
}
