package com.tuanzisama.community.event;

import cn.hutool.json.JSONUtil;
import com.tuanzisama.community.pojo.Event;
import com.tuanzisama.community.pojo.Message;
import com.tuanzisama.community.service.MessageService;
import com.tuanzisama.community.util.CommunityConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    @Autowired
    private MessageService messageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = EVENT_QUEUE),
            exchange = @Exchange(name = EVENT_EXCHANGE,type = ExchangeTypes.TOPIC),
            key = {EVENT_COMMENT,EVENT_LIKE,EVENT_FOLLOW}
    ))
    public void handleEventMessage(Event event){
        if(event==null){
            logger.error("消息异常！");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setStatus(0);
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setToId(event.getEntityUserId());
        message.setFromId(SYSTEM_USER_ID);

        Map<String,Object> content = new HashMap<>();
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        content.put("userId",event.getUserId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        message.setContent(JSONUtil.toJsonStr(content));
        messageService.addMessage(message);
    }
}
