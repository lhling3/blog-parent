package com.ling.blog.service;

import com.ling.blog.vo.Result;
import com.ling.blog.vo.TagVo;

import java.util.List;

public interface TagService {
    List<TagVo> findTagsByArticleId(Long articleId);

    Result hots(int limit);

    /**
     * 列出所有文章标签
     * @return
     */
    Result findAllTags();

    /**
     * 查询标签细节
     * @return
     */
    Result findAllDetail();

    /**
     * 根据标签id显示对应的标签细节信息
     * @return
     */
    Result tagDetailById(Long id);

}
