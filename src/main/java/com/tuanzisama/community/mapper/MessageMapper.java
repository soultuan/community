package com.tuanzisama.community.mapper;

import com.tuanzisama.community.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> selectMessageListByUserId(@Param("userId") Integer userId,@Param("offset") Integer offset,@Param("limit") Integer limit);
    Integer countMessageListByUserId(Integer userId);
    List<Message> selectDetailMessageListByConversationId(@Param("conversationId") String conversationId,@Param("offset") Integer offset,@Param("limit") Integer limit);
    Integer countDetailMessageListByConversationId(String conversationId);
    Integer countUnreadMessageList(@Param("conversationId") String conversationId,@Param("userId") Integer userId);

}
