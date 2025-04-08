package com.tuanzisama.community.service;

public interface FollowService {
    void follow(int userId,int entityType, int entityId);
    void unfollow(int userId,int entityType, int entityId);
    Long followerCount(int userId, int entityType);
    Long followeeCount(int userId,int entityType);
    boolean isFollower(int userId,int entityType, int entityId);
}
