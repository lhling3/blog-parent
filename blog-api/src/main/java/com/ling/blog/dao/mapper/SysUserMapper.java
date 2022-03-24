package com.ling.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ling.blog.dao.pojo.SysUser;
import org.apache.ibatis.annotations.Param;

public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 跟剧作者id查询作者
     * @param id
     * @return
     */
    SysUser findUserById(@Param("id") Long id);

    /**
     * 根据账户名查询用户
     * @param account
     * @return
     */
    SysUser findUserByAccount(@Param("account")String account);

    /**
     * 添加用户，用于注册用户的保存
     * @param sysUser
     */
    void addUser(@Param("sysUser") SysUser sysUser);
}
