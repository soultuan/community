package com.tuanzisama.community.mapper;

import com.tuanzisama.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(@Param("userId") Integer userId,@Param("offset") Integer offset,@Param("pageSize") Integer pageSize);

    Integer countDiscussPosts(@Param("userId") Integer userId);
}
