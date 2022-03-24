package com.ling.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.ling.blog.dao.pojo.SysUser;
import com.ling.blog.service.LoginService;
import com.ling.blog.service.SysUserService;
import com.ling.blog.utils.JWTUtils;
import com.ling.blog.vo.ErrorCode;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.params.LoginParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {
    private final SysUserService sysUserService;

    private final RedisTemplate<String,String> redisTemplate;

    private static final String slat = "mszlu!@#";

    @Autowired
    public LoginServiceImpl(SysUserService sysUserService,RedisTemplate<String,String> redisTemplate){
        this.sysUserService = sysUserService;
        this.redisTemplate = redisTemplate;
    }
    @Override
    public Result login(LoginParam loginParam) {
        //1、检测参数是否合法
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        //密码经过处理，使用加密盐
        password = DigestUtils.md5Hex(password + slat);
        //2、根据用户名和密码去user表中查询是否存在
        SysUser sysUser = sysUserService.findUser(account,password);
        //3、如果不存在，登录失败
        if(sysUser == null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        //4、存在，使用jwt生成token，返回给前端
        String token = JWTUtils.createToken(sysUser.getId());
        //5、token放入redis中，redis存储token:user信息 设置过期时间
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        // （登陆认证时，先认证token字符串是否合法，去redis认证是否存在）

        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if(stringObjectMap == null){
            return null;
        }
        String userJason = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank(userJason))return null;
        SysUser sysUser = JSON.parseObject(userJason, SysUser.class);
        return sysUser;
    }

    /**
     * 退出登录
     * @param token
     * @return
     */
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }

    /**
     * 注册功能
     * @param loginParam
     * @return
     */
    @Override
    public Result register(LoginParam loginParam) {
        /**
         * 1、判断参数是否合法
         * 2、判断账户是否存在 存在，返回账户已经被注册
         * 3、不存在，注册用户
         * 4、生成token
         * 5、存入redis并返回
         * 6、注意，加上事务，一旦中间的任何过程出现问题，需要回滚
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        SysUser sysUser = sysUserService.findUserByAccount(account);
        if(sysUser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setId(null);
        sysUser.setMobilePhoneNumber("111");
        sysUser.setSalt("111");
        sysUser.setStatus("111");
        sysUser.setEmail("111");
        this.sysUserService.save(sysUser);

        String token = JWTUtils.createToken(sysUser.getId());
        //注册好像不可以设置过期时间
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser));

        return Result.success(token);
    }
}
