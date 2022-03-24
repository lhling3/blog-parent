package com.ling.blog.service;

import com.ling.blog.vo.Result;
import com.ling.blog.vo.TagVo;

import java.util.List;

public interface TagService {
    List<TagVo> findTagsByArticleId(Long articleId);

    Result hots(int limit);
}
