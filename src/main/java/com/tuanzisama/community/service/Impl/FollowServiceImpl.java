package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.FollowService;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.RedisKeyUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService,CommunityConstant {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserService userService;

    @Override
    public void follow(int userId, int entityType, int entityId) {
        stringRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                operations.opsForZSet().add(followeeKey,String.valueOf(entityId),System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,String.valueOf(userId),System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {
        stringRedisTemplate.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                operations.multi();
                operations.opsForZSet().remove(followeeKey,String.valueOf(entityId));
                operations.opsForZSet().remove(followerKey,String.valueOf(userId));
                return operations.exec();
            }
        });
    }

    //查看实体的粉丝数量
    @Override
    public Long followerCount(int userId, int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,userId);
        return stringRedisTemplate.opsForZSet().zCard(followerKey);
    }

    //查看关注的实体对象
    @Override
    public Long followeeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return stringRedisTemplate.opsForZSet().zCard(followeeKey);
    }

    @Override
    public boolean isFollower(int userId,int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return stringRedisTemplate.opsForZSet().score(followeeKey,String.valueOf(entityId)) != null;
    }

    @Override
    public List<Map<String, Object>> selectFollowerByUserId(int userId,int offset,int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (ids == null || ids.size() == 0) {
            return null;
        }
        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        for (String id : ids) {
            Map<String, Object> map = new HashMap<String, Object>();
            int idNum = Integer.parseInt(id);
            User user = userService.selectUserById(idNum);
            map.put("user", user);
            Double Time = stringRedisTemplate.opsForZSet().score(followerKey,id);
            map.put("followTime",new Date(Time.longValue()));
            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> selectFolloweeByUserId(int userId,int offset,int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<String> ids = stringRedisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (ids == null || ids.size() == 0) {
            return null;
        }

        List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
        for (String id : ids) {
            Map<String, Object> map = new HashMap<String, Object>();
            int idNum = Integer.parseInt(id);
            User user = userService.selectUserById(idNum);
            map.put("user", user);
            Double Time = stringRedisTemplate.opsForZSet().score(followeeKey,id);
            map.put("followTime",new Date(Time.longValue()));
            result.add(map);
        }
        return result;
    }
}
