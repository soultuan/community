package com.tuanzisama.community.util;

public interface CommunityConstant {
    Integer ACTIVATION_SUCCESS = 0;
    Integer ACTIVATION_REPEAT = 1;
    Integer ACTIVATION_FAILURE = 2;

    Integer DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    Integer REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    Integer ENTITY_TYPE_POST = 1;
    Integer ENTITY_TYPE_COMMENT = 2;
}
