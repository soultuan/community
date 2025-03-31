package com.tuanzisama.community.mapper;

import com.tuanzisama.community.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectCommentByEntity(@Param("entityType") Integer entityType,@Param("entityId") Integer entityId,@Param("offset") Integer offset,@Param("limit") Integer limit);
    Integer countCommentByEntity(@Param("entityType") Integer entityType,@Param("entityId") Integer entityId);
}
