package com.tuanzisama.community.service;

public interface LikeService {
    void like(int userId,int entityType, int entityId);
    Long countLike(int entityType, int entityId);
    Integer likeStatus(int userId,int entityType, int entityId);
}
