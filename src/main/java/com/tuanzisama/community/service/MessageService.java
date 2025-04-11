package com.tuanzisama.community.service;

import com.tuanzisama.community.pojo.Message;

import java.util.List;

public interface MessageService {
    List<Message> selectMessageListByUserId(Integer userId, Integer offset, Integer limit);
    Integer countMessageListByUserId(Integer userId);
    List<Message> selectDetailMessageListByConversationId(String conversationId,Integer offset,Integer limit);
    Integer countDetailMessageListByConversationId(String conversationId);
    Integer countUnreadMessageList(String conversationId,Integer userId);
    Integer addMessage(Message message);
    Integer readUnreadMessage(List<Integer> ids,Integer status);
    Message selectLatestEvent(int userId,String entityType);
    int countUnreadEvent(int userId,String entityType);
    int countEvent(int userId,String entityType);
    List<Message> selectNotices(int userId,String topic,int offset,int limit);
}
