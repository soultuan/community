package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.mapper.DiscussPostMapper;
import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Override
    public List<DiscussPost> selectDiscussPosts(Integer userId, Integer offset, Integer pageSize) {
        return discussPostMapper.selectDiscussPosts(userId, offset, pageSize);
    }

    @Override
    public Integer countDiscussPosts(Integer userId) {
        return discussPostMapper.countDiscussPosts(userId);
    }
}
