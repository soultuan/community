package com.tuanzisama.community.service;

import com.tuanzisama.community.pojo.LoginTicket;
import com.tuanzisama.community.pojo.User;

import java.util.Map;

public interface UserService {
    User selectUserById(Integer userId);

    Map<String,Object> register(User user);

    Integer activation(Integer userId, String activationCode);

    Map<String,Object> login(String username, String password, Boolean rememberme);

    void logout(String ticket);

    LoginTicket selectByTicket(String ticket);

    Integer updateHeaderUrl(Integer id, String headerUrl);

    void updatePassword(Integer id, String newPassword);

    User selectUserByName(String username);
}
