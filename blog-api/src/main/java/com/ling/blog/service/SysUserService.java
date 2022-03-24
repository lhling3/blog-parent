package com.ling.blog.service;

import com.ling.blog.dao.pojo.SysUser;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.UserVo;

public interface SysUserService {

    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);

    //根据token查询用户信息
    Result findUserByToken(String token);

    //根据账户查找用户
    SysUser findUserByAccount(String account);

    //保存注册用户
    void save(SysUser sysUser);

    //根据id获取UserVo
    UserVo findUserVoById(Long id);
}
