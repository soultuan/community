package com.tuanzisama.community.mapper;

import com.tuanzisama.community.pojo.LoginTicket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    LoginTicket selectByTicket(String ticket);

    Integer insertLoginTicket(LoginTicket loginTicket);

    Integer updateStatus(@Param("ticket") String ticket,@Param("status") Integer status);
}
