package com.tuanzisama.community.mapper;

import com.tuanzisama.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User selectUserById(Integer userId);

    User selectUserByEmail(String email);

    User selectUserByUsername(String username);

    void insertUser(User user);

    void updateStatus(@Param("userId") Integer userId,@Param("status") Integer status);

    Integer updateHeaderUrl(@Param("id") Integer id,@Param("headerUrl") String headerUrl);

    void updatePassword(Integer id, String newPassword);

    User selectUserByName(String username);
}
