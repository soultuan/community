package com.tuanzisama.community.config;

import com.tuanzisama.community.Interceptor.LoginInterceptor;
import com.tuanzisama.community.Interceptor.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).excludePathPatterns(
                "**/*.css",
                "**/*.js",
                "**/*.png",
                "**/*.jpg",
                "**/*.jpeg");
        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns(
                "**/*.css",
                "**/*.js",
                "**/*.png",
                "**/*.jpg",
                "**/*.jpeg"
        );
    }
}
