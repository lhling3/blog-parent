package com.ling.blog.handler;

import com.alibaba.fastjson.JSON;
import com.ling.blog.dao.pojo.SysUser;
import com.ling.blog.service.LoginService;
import com.ling.blog.utils.UserThreadLocal;
import com.ling.blog.vo.ErrorCode;
import com.ling.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    private final LoginService loginService;

    @Autowired
    public LoginInterceptor(LoginService loginService){
        this.loginService = loginService;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在执行handler方法前执行
        /**
         * 1、需要判断请求的接口路径是否为HandlerMethod
         * 2、判断token是否为空，如果为空，未登录
         * 3、不为空，登录验证 loginService checkToken
         * 4、如果认证成功，放行
         */
        if(!(handler instanceof HandlerMethod)){
            //handler可能是RequestResourceHandler,
            // springboot访问静态资源默认去static目录去查询，这种直接放行
            return true;
        }
        String token = request.getHeader("Authorization");

        //加入日志  添加@Slf4j注解创建log对象
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if(StringUtils.isBlank(token)){
            //未登录
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser = loginService.checkToken(token);
        if(sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //如果认证成功，放行
        //希望在controller中直接获取用户信息 怎么获取
        //将用户信息放入ThreadLocal中
        UserThreadLocal.put(sysUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //用完了ThreadLocal，需要删掉
        //如果不删除，会有内存泄露的风险
        UserThreadLocal.remove();
    }
}
