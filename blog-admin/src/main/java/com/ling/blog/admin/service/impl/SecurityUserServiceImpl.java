package com.ling.blog.admin.service.impl;

import com.ling.blog.admin.dao.pojo.Admin;
import com.ling.blog.admin.service.AdminService;
import com.ling.blog.admin.service.SecurityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SecurityUserServiceImpl implements SecurityUserService, UserDetailsService {
    private AdminService adminService;
    @Autowired
    public SecurityUserServiceImpl(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //登录时，会把username传递到这里
        //通过username查询admin表，如果admin存在，将密码告诉 spring security
        //如果不存在，返回null，认证失败了
        Admin admin = adminService.findAdminByUsername(username);
        if(admin == null){
            //登陆失败
            return null;
        }
        UserDetails userDetails = new User(username,admin.getPassword(),new ArrayList<>());
        return userDetails;
    }
}
