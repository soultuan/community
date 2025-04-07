package com.tuanzisama.community.advice;

import com.tuanzisama.community.util.CommunityUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("服务器异常：" + e.getMessage());
        for(StackTraceElement stackTraceElement : e.getStackTrace()) {
            log.error(stackTraceElement.toString());
        }

        String header = request.getHeader("x-requested-with");
        if(header != null && header.equals("XMLHttpRequest")) {
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJsonString(1,"服务器异常！"));
        }else{
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
