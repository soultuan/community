package com.tuanzisama.community.service;

import com.tuanzisama.community.pojo.DiscussPost;

import java.util.List;

public interface DiscussPostService {
    List<DiscussPost> selectDiscussPosts(Integer userId, Integer offset, Integer pageSize);

    Integer countDiscussPosts(Integer userId);

    void insertDiscussPost(String title, String content);

    DiscussPost selectDiscussPostById(Integer discussPostId);
}
