package com.tuanzisama.community.service;

public interface LikeService {
    void like(int userId,int entityType, int entityId,int likeUserId);
    Long countLike(int entityType, int entityId);
    Integer likeStatus(int userId,int entityType, int entityId);
    Integer countUserLike(int userId);
}
