package com.ling.blog.admin.service;

import com.ling.blog.admin.dao.pojo.Admin;
import com.ling.blog.admin.dao.pojo.Permission;

import java.util.List;

public interface AdminService {
    Admin findAdminByUsername(String username);


    List<Permission> findPermissionByAdminId(Long id);
}
