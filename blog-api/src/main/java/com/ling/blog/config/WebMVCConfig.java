package com.ling.blog.config;

import com.ling.blog.handler.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    private LoginInterceptor loginInterceptor;

    @Autowired
    public WebMVCConfig(LoginInterceptor loginInterceptor){
        this.loginInterceptor = loginInterceptor;
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //跨域配置，不同端口的访问
        registry.addMapping("/**").allowedOrigins("http://localhost:8080");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //暂时先拦截test接口，后续遇到实际需要拦截接口时再配置
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/test").addPathPatterns("/comments/create/change");
    }
}
