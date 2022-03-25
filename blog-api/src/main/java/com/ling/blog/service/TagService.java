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
}
