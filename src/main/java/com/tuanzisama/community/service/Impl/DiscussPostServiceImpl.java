package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.mapper.DiscussPostMapper;
import com.tuanzisama.community.pojo.DiscussPost;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.DiscussPostService;
import com.tuanzisama.community.util.SensitiveFilter;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPost> selectDiscussPosts(Integer userId, Integer offset, Integer pageSize) {
        return discussPostMapper.selectDiscussPosts(userId, offset, pageSize);
    }

    @Override
    public Integer countDiscussPosts(Integer userId) {
        return discussPostMapper.countDiscussPosts(userId);
    }

    @Override
    public void insertDiscussPost(String title, String content) {
        if(StringUtils.isEmpty(title) || StringUtils.isEmpty(content)){
            throw new IllegalArgumentException("参数不能为空！");
        }
        User user = ThreadLocalUtil.get();
        DiscussPost discussPost = new DiscussPost();
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);
        title = sensitiveFilter.sensitiveFilt(title);
        content = sensitiveFilter.sensitiveFilt(content);
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setUserId(String.valueOf(user.getId()));
        discussPost.setCreateTime(new Date());
        discussPost.setScore(0.0);
        discussPost.setStatus(0);
        discussPost.setType(0);
        discussPost.setCommentCount(0);
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Override
    public DiscussPost selectDiscussPostById(Integer discussPostId) {
        return discussPostMapper.selectDiscussPostById(discussPostId);
    }
}
