package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.mapper.UserMapper;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService, CommunityConstant {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public User selectUserById(Integer userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (user==null){
            throw new IllegalArgumentException("参数为空");
        }
        if(StringUtils.isEmpty(user.getUsername())){
            map.put("usernameMsg","用户名为空");
            return map;
        }
        if (userMapper.selectUserByUsername(user.getUsername())!=null){
            map.put("usernameMsg","用户名重复");
            return map;
        };

        if(StringUtils.isEmpty(user.getEmail())){
            map.put("emailMsg","邮箱为空");
            return map;
        }
        if(userMapper.selectUserByEmail(user.getEmail())!=null){
            map.put("emailMsg","邮箱重复");
            return map;
        }
        if(StringUtils.isEmpty(user.getPassword())){
            map.put("passwordMsg","密码为空");
            return map;
        }

        user.setStatus(0);
        user.setType(0);
        user.setCreateTime(new Date());
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl("http://images.nowcoder.com/head/"+ new Random().nextInt(1000)+"t.png");
        userMapper.insertUser(user);

        Context context = new Context();
        context.setVariable("email",user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),"激活邮件",content);
        return map;
    }

    @Override
    public Integer activation(Integer userId, String activationCode) {
        User user = userMapper.selectUserById(userId);
        if(user.getStatus()==1){
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(activationCode)) {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
}
