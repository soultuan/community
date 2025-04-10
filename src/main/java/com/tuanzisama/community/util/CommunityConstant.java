package com.tuanzisama.community.util;

public interface CommunityConstant {
    Integer ACTIVATION_SUCCESS = 0;
    Integer ACTIVATION_REPEAT = 1;
    Integer ACTIVATION_FAILURE = 2;

    Integer DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    Integer REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    Integer ENTITY_TYPE_POST = 1;
    Integer ENTITY_TYPE_COMMENT = 2;
    Integer ENTITY_TYPE_USER = 3;

    //交换机
    String EVENT_EXCHANGE = "event.topic";
    //事件队列
    String EVENT_QUEUE = "event.queue";
    //主题：评论
    String EVENT_COMMENT = "comment";
    //主题：点赞
    String EVENT_LIKE = "like";
    //主题：关注
    String EVENT_FOLLOW = "follow";

    //系统ID
    int SYSTEM_USER_ID = 1;
}
