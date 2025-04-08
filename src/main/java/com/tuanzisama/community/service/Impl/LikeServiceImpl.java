package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.service.LikeService;
import com.tuanzisama.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void like(int userId, int entityType, int entityId) {
        String redisKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        if(stringRedisTemplate.opsForSet().isMember(redisKey, String.valueOf(userId))){
            stringRedisTemplate.opsForSet().remove(redisKey, String.valueOf(userId));
        }else{
            stringRedisTemplate.opsForSet().add(redisKey, String.valueOf(userId));
        }
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
}
