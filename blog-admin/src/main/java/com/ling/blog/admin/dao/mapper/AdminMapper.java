package com.ling.blog.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ling.blog.admin.dao.pojo.Admin;
import com.ling.blog.admin.dao.pojo.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminMapper extends BaseMapper<Admin> {
    List<Permission> findPermissionByAdminId(@Param("id") Long id);
}
