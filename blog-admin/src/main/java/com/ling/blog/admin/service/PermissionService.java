package com.ling.blog.admin.service;

import com.ling.blog.admin.dao.pojo.Permission;
import com.ling.blog.admin.model.params.PageParam;
import com.ling.blog.admin.vo.Result;

public interface PermissionService {
    /**
     * 列出所有通过名单 即管理台
     * @param pageParam
     * @return
     */
    Result listPermission(PageParam pageParam);

    Result add(Permission permission);

    Result update(Permission permission);

    Result delete(Long id);
}
