package com.ling.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ling.blog.dao.mapper.SysUserMapper;
import com.ling.blog.dao.pojo.SysUser;
import com.ling.blog.service.LoginService;
import com.ling.blog.service.SysUserService;
import com.ling.blog.utils.JWTUtils;
import com.ling.blog.vo.ErrorCode;
import com.ling.blog.vo.LoginUserVo;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysUserServiceImpl implements SysUserService {
    private final SysUserMapper sysUserMapper;

    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    public SysUserServiceImpl(SysUserMapper sysUserMapper,RedisTemplate<String,String> redisTemplate){
        this.sysUserMapper = sysUserMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.findUserById(id);
        if(sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("匿名用户");
        }
        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1、token合法性校验
         * 是否为空，解析是否成功 redis是否存在
         * 2、如果校验失败，返回错误
         * 3、如果成功，返回对应结果 LoginUserVo
         */
        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map == null){
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)){
            return Result.fail(ErrorCode.NO_LOGIN.getCode(),ErrorCode.NO_LOGIN.getMsg());
        }
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setNickname(sysUser.getNickname());
        return Result.success(loginUserVo);
    }

    /**
     * 根据账户查询用户
     * @param account
     * @return
     */
    @Override
    public SysUser findUserByAccount(String account) {
        /*LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);*/
        return sysUserMapper.findUserByAccount(account);
    }

    @Override
    public void save(SysUser sysUser) {
        //保存用户，id会自动生成
        //默认生成的id是分布式id，采用雪花算法
        //this.sysUserMapper.insert(sysUser);
       // String account = sysUser.getAccount();
        sysUserMapper.addUser(sysUser);
    }

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.findUserById(id);
        if(sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("匿名用户");
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        }
        UserVo userVo = new UserVo();
        userVo.setId(sysUser.getId());
        userVo.setNickname(sysUser.getNickname());
        userVo.setAvatar(sysUser.getAvatar());
        return userVo;
    }
}
