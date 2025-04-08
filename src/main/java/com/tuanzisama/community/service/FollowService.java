package com.tuanzisama.community.service;

import java.util.List;
import java.util.Map;

public interface FollowService {
    void follow(int userId,int entityType, int entityId);
    void unfollow(int userId,int entityType, int entityId);
    Long followerCount(int userId, int entityType);
    Long followeeCount(int userId,int entityType);
    boolean isFollower(int userId,int entityType, int entityId);
    List<Map<String,Object>> selectFollowerByUserId(int userId,int offset,int limit);
    List<Map<String,Object>> selectFolloweeByUserId(int userId,int offset,int limit);
}
