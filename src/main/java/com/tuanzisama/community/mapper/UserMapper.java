package com.tuanzisama.community.mapper;

import com.tuanzisama.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectUserById(Integer userId);
}
