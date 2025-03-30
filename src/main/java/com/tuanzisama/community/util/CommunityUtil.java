package com.tuanzisama.community.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Map;
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

    public static String getJsonString(Integer code, String msg, Map<String,Object> data){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",code);
        jsonObject.put("msg",msg);
        if(data!=null){
            for(Map.Entry<String,Object> entry:data.entrySet()){
                jsonObject.put(entry.getKey(),entry.getValue());
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJsonString(Integer code, String msg){
        return getJsonString(code,msg,null);
    }

    public static String getJsonString(Integer code){
        return getJsonString(code,null,null);
    }
}
