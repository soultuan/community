package com.tuanzisama.community.controller;

import com.tuanzisama.community.pojo.Message;
import com.tuanzisama.community.pojo.Page;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.MessageService;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/letter")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public String getMessageList(Model model, Page page) {
        User user = ThreadLocalUtil.get();
        page.setPath("/letter/list");
        page.setLimit(5);
        page.setRows(messageService.countMessageListByUserId(user.getId()));

        List<Message> messages = messageService.selectMessageListByUserId(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String,Object>> list = new ArrayList<>();
        if(messages != null && messages.size() > 0) {
            for (Message message : messages) {
                Map<String,Object> map = new HashMap<>();
                map.put("conversation",message);
                int target = message.getFromId()==user.getId()?message.getToId():message.getFromId();
                map.put("target",userService.selectUserById(target));
                map.put("letterCount",messageService.countDetailMessageListByConversationId(message.getConversationId()));
                map.put("unreadCount",messageService.countUnreadMessageList(message.getConversationId(),user.getId()));
                list.add(map);
            }
        }

        model.addAttribute("conversations",list);
        model.addAttribute("letterUnreadCount",messageService.countUnreadMessageList(null,user.getId()));
        return "/site/letter";
    }

    @GetMapping("/detail/{conversationId}")
    public String getMessageDetail(@PathVariable("conversationId") String conversationId, Model model,Page page) {
        page.setPath("/letter/detail/" + conversationId);
        page.setLimit(5);
        page.setRows(messageService.countDetailMessageListByConversationId(conversationId));

        List<Map<String,Object>> list = new ArrayList<>();
        List<Message> messages = messageService.selectDetailMessageListByConversationId(conversationId, page.getOffset(), page.getLimit());
        if(messages != null && messages.size() > 0) {
            for (Message message : messages) {
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.selectUserById(message.getFromId()));
                list.add(map);
            }
        }
        model.addAttribute("letters",list);
        model.addAttribute("target",getFromUser(conversationId));

        return "/site/letter-detail";
    }

    private User getFromUser(String conversationId) {
        User user = ThreadLocalUtil.get();
        String[] userIds = conversationId.split("_");
        String userOne = userIds[0];
        String userTwo = userIds[1];
        return Integer.parseInt(userOne)==user.getId()?userService.selectUserById(user.getId()):userService.selectUserById(Integer.parseInt(userTwo));

    }
}
