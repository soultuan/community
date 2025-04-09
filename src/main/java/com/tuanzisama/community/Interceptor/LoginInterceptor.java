package com.tuanzisama.community.Interceptor;

import com.tuanzisama.community.mapper.LoginTicketMapper;
import com.tuanzisama.community.mapper.UserMapper;
import com.tuanzisama.community.pojo.LoginTicket;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.service.UserService;
import com.tuanzisama.community.util.CookieUtil;
import com.tuanzisama.community.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            LoginTicket loginTicket = userService.selectByTicket(ticket);
            if(loginTicket!=null&&loginTicket.getStatus()!=null&&loginTicket.getStatus()==0&&loginTicket.getExpired().after(new Date())) {
                User user = userService.selectUserById(loginTicket.getUserId());
                ThreadLocalUtil.set(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = ThreadLocalUtil.get();
        if (user != null&&modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtil.remove();
    }
}
