package com.tuanzisama.community.service;

import com.tuanzisama.community.pojo.User;

import java.util.Map;

public interface UserService {
    User selectUserById(Integer userId);

    Map<String,Object> register(User user);

    Integer activation(Integer userId, String activationCode);
}
