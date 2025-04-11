package com.tuanzisama.community.controller;

import cn.hutool.json.JSONUtil;
import com.tuanzisama.community.pojo.Message;
import com.tuanzisama.community.pojo.Page;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.MessageService;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CommunityConstant;
import com.tuanzisama.community.util.CommunityUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @GetMapping("/letter/list")
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
        model.addAttribute("noticeUnreadCount",messageService.countUnreadEvent(user.getId(),null));
        return "/site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
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

        List<Integer> ids = setUnreadToRead(messages);
        if(!ids.isEmpty()) {
            messageService.readUnreadMessage(ids,1);
        }
        return "/site/letter-detail";
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendMessage(String toName,String content) {
        User user = ThreadLocalUtil.get();
        User toUser = userService.selectUserByName(toName);
        if(toUser==null) {
            return CommunityUtil.getJsonString(1,"用户不存在！");
        }
        Message message = new Message();
        message.setFromId(user.getId());
        message.setToId(toUser.getId());
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        if(user.getId()<toUser.getId()) {
            message.setConversationId(user.getId() + "_" + toUser.getId());
        }else {
            message.setConversationId(toUser.getId() + "_" + user.getId());
        }

        messageService.addMessage(message);
        return CommunityUtil.getJsonString(0);
    }

    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        User user = ThreadLocalUtil.get();
        //查询评论类通知
        Message message = messageService.selectLatestEvent(user.getId(), EVENT_COMMENT);
        Map<String,Object> messageVO = new HashMap<>();
        if(message != null) {
            messageVO.put("message",message);

            String content = message.getContent();
            content = HtmlUtils.htmlUnescape(content);
            Map<String,Object> eventMap = JSONUtil.toBean(content, HashMap.class);

            if(eventMap != null) {
                messageVO.put("entityType",eventMap.get("entityType"));
                messageVO.put("entityId",eventMap.get("entityId"));
                messageVO.put("postId",eventMap.get("postId"));
                messageVO.put("user",userService.selectUserById((Integer) eventMap.get("userId")));
            }
            messageVO.put("count",messageService.countEvent(user.getId(), EVENT_COMMENT));
            messageVO.put("unread",messageService.countUnreadEvent(user.getId(), EVENT_COMMENT));
        }
        model.addAttribute("commentNotice",messageVO);

        //查询点赞类通知
        message = messageService.selectLatestEvent(user.getId(), EVENT_LIKE);
        messageVO = new HashMap<>();
        if(message != null) {
            messageVO.put("message",message);

            String content = message.getContent();
            content = HtmlUtils.htmlUnescape(content);
            Map<String,Object> eventMap = JSONUtil.toBean(content, HashMap.class);

            if(eventMap != null) {
                messageVO.put("entityType",eventMap.get("entityType"));
                messageVO.put("entityId",eventMap.get("entityId"));
                messageVO.put("postId",eventMap.get("postId"));
                messageVO.put("user",userService.selectUserById((Integer) eventMap.get("userId")));
            }
            messageVO.put("count",messageService.countEvent(user.getId(), EVENT_LIKE));
            messageVO.put("unread",messageService.countUnreadEvent(user.getId(), EVENT_LIKE));
        }
        model.addAttribute("likeNotice",messageVO);

        //查询关注类通知
        message = messageService.selectLatestEvent(user.getId(), EVENT_FOLLOW);
        messageVO = new HashMap<>();
        if(message != null) {
            messageVO.put("message",message);

            String content = message.getContent();
            content = HtmlUtils.htmlUnescape(content);
            Map<String,Object> eventMap = JSONUtil.toBean(content, HashMap.class);

            if(eventMap != null) {
                messageVO.put("entityType",eventMap.get("entityType"));
                messageVO.put("entityId",eventMap.get("entityId"));
                messageVO.put("user",userService.selectUserById((Integer) eventMap.get("userId")));
            }
            messageVO.put("count",messageService.countEvent(user.getId(), EVENT_FOLLOW));
            messageVO.put("unread",messageService.countUnreadEvent(user.getId(), EVENT_FOLLOW));
        }
        model.addAttribute("followNotice",messageVO);
        model.addAttribute("letterUnreadCount",messageService.countUnreadMessageList(null,user.getId()));
        model.addAttribute("noticeUnreadCount",messageService.countUnreadEvent(user.getId(),null));
        return "/site/notice";
    }

    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic,Model model,Page page) {
        User user = ThreadLocalUtil.get();
        page.setRows(messageService.countEvent(user.getId(),topic));
        page.setLimit(5);
        page.setPath("/notice/detail/"+topic);

        List<Message> messages = messageService.selectNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVOList = new ArrayList<>();
        if(messages != null && messages.size() > 0) {
            for(Message message : messages) {
                Map<String,Object> noticeVO = new HashMap<>();
                //通知
                noticeVO.put("notice",message);
                //内容
                String content = message.getContent();
                content = HtmlUtils.htmlUnescape(content);
                Map<String,Object> eventMap = JSONUtil.toBean(content, HashMap.class);
                noticeVO.put("user",userService.selectUserById((Integer) eventMap.get("userId")));
                noticeVO.put("entityType",eventMap.get("entityType"));
                noticeVO.put("entityId",eventMap.get("entityId"));
                noticeVO.put("postId",eventMap.get("postId"));
                //通知方
                noticeVO.put("fromUser",userService.selectUserById(message.getFromId()));
                noticeVOList.add(noticeVO);
            }
        }
        model.addAttribute("notices",noticeVOList);

        //设置已读
        List<Integer> ids = setUnreadToRead(messages);
        if(!ids.isEmpty()){
            messageService.readUnreadMessage(ids,1);
        }
        return "/site/notice-detail";
    }

    private User getFromUser(String conversationId) {
        User user = ThreadLocalUtil.get();
        String[] userIds = conversationId.split("_");
        String userOne = userIds[0];
        String userTwo = userIds[1];
        return Integer.parseInt(userOne)==user.getId()?userService.selectUserById(user.getId()):userService.selectUserById(Integer.parseInt(userTwo));

    }

    private List<Integer> setUnreadToRead(List<Message> messageList){
        List<Integer> ids = new ArrayList<>();
        User user = ThreadLocalUtil.get();
        if(messageList != null && messageList.size() > 0) {
            for (Message message : messageList) {
                if(message.getToId().equals(user.getId())&&message.getStatus()==0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
}
