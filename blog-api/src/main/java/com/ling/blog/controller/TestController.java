package com.ling.blog.controller;


import com.ling.blog.dao.pojo.SysUser;
import com.ling.blog.utils.UserThreadLocal;
import com.ling.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        SysUser sysUser = UserThreadLocal.get();
        System.out.println(sysUser);
        return Result.success(null);
    }
}