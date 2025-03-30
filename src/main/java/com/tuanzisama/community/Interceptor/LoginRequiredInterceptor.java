package com.tuanzisama.community.Interceptor;

import com.tuanzisama.community.annotation.LoginRequired;
import com.tuanzisama.community.pojo.User;
import com.tuanzisama.community.util.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = ThreadLocalUtil.get();
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if(methodAnnotation != null&&user==null) {
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
