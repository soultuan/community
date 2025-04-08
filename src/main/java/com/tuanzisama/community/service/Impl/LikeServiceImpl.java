package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.service.LikeService;
import com.tuanzisama.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void like(int userId, int entityType, int entityId,int likedUserId) {
        stringRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(likedUserId);

                Boolean flag = operations.opsForSet().isMember(entityLikeKey, String.valueOf(userId));
                operations.multi();
                if (flag) {
                    operations.opsForSet().remove(entityLikeKey, String.valueOf(userId));
                    operations.opsForValue().decrement(userLikeKey);
                }else{
                    operations.opsForSet().add(entityLikeKey, String.valueOf(userId));
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }

    @Override
    public Long countLike(int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return stringRedisTemplate.opsForSet().size(redisKey);
    }

    @Override
    public Integer likeStatus(int userId, int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return stringRedisTemplate.opsForSet().isMember(redisKey, String.valueOf(userId)) ? 1 : 0;
    }

    @Override
    public Integer countUserLike(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        return stringRedisTemplate.opsForValue().get(userLikeKey)==null ? 0 : Integer.valueOf(stringRedisTemplate.opsForValue().get(userLikeKey));
    }
}
