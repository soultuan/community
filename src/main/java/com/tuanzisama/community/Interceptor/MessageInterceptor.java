package com.tuanzisama.community.Interceptor;

import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.MessageService;
import com.tuanzisama.community.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = ThreadLocalUtil.get();
        if(user != null && modelAndView != null) {
            int unreadLetter = messageService.countUnreadMessageList(null, user.getId());
            int unreadEvent = messageService.countUnreadEvent(user.getId(), null);
            modelAndView.addObject("allUnreadCount", unreadLetter+unreadEvent);
        }
    }
}
