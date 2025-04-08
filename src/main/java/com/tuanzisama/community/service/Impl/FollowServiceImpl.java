package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.FollowService;
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

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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
}
