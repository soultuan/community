package com.tuanzisama.community.event;

import com.tuanzisama.community.pojo.Event;
import com.tuanzisama.community.util.CommunityConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventProducer implements CommunityConstant {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //处理事件
    public void fireEvent(Event event) {
        rabbitTemplate.convertAndSend(EVENT_EXCHANGE,event.getTopic(),event);
    }

}
