package com.ling.blog.service;

import com.ling.blog.vo.CategoryVo;

import java.util.List;

public interface CategoryService {

    /**
     * 根据id查询文章类别
     * @param categoryId
     * @return
     */
    CategoryVo findCategoryById(Long categoryId);
}
