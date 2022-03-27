package com.ling.blog.admin.controller;

import com.ling.blog.admin.dao.pojo.Permission;
import com.ling.blog.admin.model.params.PageParam;
import com.ling.blog.admin.service.PermissionService;
import com.ling.blog.admin.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private PermissionService permissionService;

    @Autowired
    public AdminController(PermissionService permissionService){
        this.permissionService = permissionService;
    }

    /**
     * 列出所有通过名单 即管理台
     * @param pageParam
     * @return
     */
    @PostMapping("/permission/permissionList")
    public Result permissionList(@RequestBody PageParam pageParam){
        return permissionService.listPermission(pageParam);
    }

    @PostMapping("permission/add")
    public Result add(@RequestBody Permission permission){
        return permissionService.add(permission);
    }

    @PostMapping("permission/update")
    public Result update(@RequestBody Permission permission){
        return permissionService.update(permission);
    }

    @GetMapping("permission/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        return permissionService.delete(id);
    }
}
