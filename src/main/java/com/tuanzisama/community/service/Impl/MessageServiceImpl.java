package com.tuanzisama.community.service.Impl;

import com.tuanzisama.community.mapper.MessageMapper;
import com.tuanzisama.community.pojo.Message;
import com.tuanzisama.community.service.MessageService;
import com.tuanzisama.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> selectMessageListByUserId(Integer userId,Integer offset,Integer limit) {
        return messageMapper.selectMessageListByUserId(userId,offset,limit);
    }

    public Integer countMessageListByUserId(Integer userId) {
        return messageMapper.countMessageListByUserId(userId);
    }

    public List<Message> selectDetailMessageListByConversationId(String conversationId,Integer offset,Integer limit) {
        return messageMapper.selectDetailMessageListByConversationId(conversationId,offset,limit);
    }

    public Integer countDetailMessageListByConversationId(String conversationId) {
        return messageMapper.countDetailMessageListByConversationId(conversationId);
    }

    public Integer countUnreadMessageList(String conversationId,Integer userId) {
        return messageMapper.countUnreadMessageList(conversationId,userId);
    }

    @Override
    public Integer addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.sensitiveFilt(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    @Override
    public Integer readUnreadMessage(List<Integer> ids, Integer status) {
        return messageMapper.updateMessageWithStatus(ids,status);
    }
}
