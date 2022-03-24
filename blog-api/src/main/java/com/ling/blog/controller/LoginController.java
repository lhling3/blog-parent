package com.ling.blog.controller;

import com.ling.blog.service.LoginService;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.params.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService){
        this.loginService = loginService;
    }
    @PostMapping
    public Result login(@RequestBody LoginParam loginParam){
        return loginService.login(loginParam);
    }
}
