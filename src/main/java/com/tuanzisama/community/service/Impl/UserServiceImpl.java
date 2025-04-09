package com.tuanzisama.community.service.Impl;

import cn.hutool.json.JSONUtil;
import com.tuanzisama.community.mapper.LoginTicketMapper;
import com.tuanzisama.community.mapper.UserMapper;
import com.tuanzisama.community.pojo.LoginTicket;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.MailClient;
import com.tuanzisama.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
//    @Autowired
//    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public User selectUserById(Integer userId) {
//        return userMapper.selectUserById(userId);
        User userInCache = getUserInCache(userId);
        if(userInCache==null){
            userInCache = initUserInCache(userId);
        }
        return userInCache;
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
            deleteUserInCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, Boolean rememberme) {
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isEmpty(username)){
            map.put("usernameMsg","用户名不能为空");
            return map;
        }
        if(StringUtils.isEmpty(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        User user = userMapper.selectUserByUsername(username);
        if(user==null){
            map.put("usernameMsg","用户不存在");
            return map;
        }
        if(!CommunityUtil.md5(password + user.getSalt()).equals(user.getPassword())){
            map.put("passwordMsg","密码错误");
            return map;
        }
        if(user.getStatus()==0){
            map.put("usernameMsg","账号未激活");
            return map;
        }

        Integer expiredTime = rememberme?CommunityConstant.REMEMBER_EXPIRED_SECONDS:CommunityConstant.REMEMBER_EXPIRED_SECONDS;
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredTime * 1000));
        String ticket = CommunityUtil.generateUUID();
        loginTicket.setTicket(ticket);

        //登录凭证存入redis
        String redisTicketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        String loginTicketJsonStr = JSONUtil.toJsonStr(loginTicket);
        stringRedisTemplate.opsForValue().set(redisTicketKey,loginTicketJsonStr);

        map.put("ticket",ticket);
        return map;
    }

    @Override
    public void logout(String ticket) {
        String redisTicketKey = RedisKeyUtil.getTicketKey(ticket);
        String loginTicketJsonStr = stringRedisTemplate.opsForValue().get(redisTicketKey);
        LoginTicket loginTicket = JSONUtil.toBean(loginTicketJsonStr, LoginTicket.class);
        loginTicket.setStatus(1);
        String jsonStr = JSONUtil.toJsonStr(loginTicket);
        stringRedisTemplate.opsForValue().set(redisTicketKey,jsonStr);
    }

    @Override
    public LoginTicket selectByTicket(String ticket) {
        String redisTicketKey = RedisKeyUtil.getTicketKey(ticket);
        String loginTicketJsonStr = stringRedisTemplate.opsForValue().get(redisTicketKey);
        return JSONUtil.toBean(loginTicketJsonStr, LoginTicket.class);
    }

    @Override
    public Integer updateHeaderUrl(Integer id, String headerUrl) {
        Integer rows = userMapper.updateHeaderUrl(id, headerUrl);
        deleteUserInCache(id);
        return rows;
    }

    @Override
    public void updatePassword(Integer id, String newPassword) {
        userMapper.updatePassword(id,newPassword);
        deleteUserInCache(id);
    }

    @Override
    public User selectUserByName(String username) {
        return userMapper.selectUserByName(username);
    }

    //从redis查询用户缓存
    private User getUserInCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        String userJson = stringRedisTemplate.opsForValue().get(userKey);
        if(StringUtils.isEmpty(userJson)){
            return null;
        }
        return JSONUtil.toBean(userJson,User.class);
    }

    //缓存用户信息到redis
    private User initUserInCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        User user = userMapper.selectUserById(userId);
        String jsonStr = JSONUtil.toJsonStr(user);
        stringRedisTemplate.opsForValue().set(userKey,jsonStr,3600, TimeUnit.SECONDS);
        return user;
    }

    //更新数据库的时候删除缓存
    private void deleteUserInCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        stringRedisTemplate.delete(userKey);
    }

}
