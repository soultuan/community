package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.mapper.UserMapper;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectUserById(Integer userId) {
        return userMapper.selectUserById(userId);
    }
}
