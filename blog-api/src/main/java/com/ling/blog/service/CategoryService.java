package com.ling.blog.service;

import com.ling.blog.vo.CategoryVo;
import com.ling.blog.vo.Result;

import java.util.List;

public interface CategoryService {

    /**
     * 根据id查询文章类别
     * @param categoryId
     * @return
     */
    CategoryVo findCategoryById(Long categoryId);

    /**
     * 列出所有文章分类
     * @return
     */
    Result findAllCategory();

    /**
     * 文章细节分类
     * @return
     */
    Result findAllDetail();

    /**
     * 根据类别id显示类别详情
     * @param id
     * @return
     */
    Result categoryDetailById(Long id);
}
