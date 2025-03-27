package com.tuanzisama.community.util;

import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串
    public static String generateUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    //md5密码加密
    public static String md5(String str){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }
}
