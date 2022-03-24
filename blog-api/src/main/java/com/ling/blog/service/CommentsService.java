package com.ling.blog.service;

import com.ling.blog.vo.Result;

public interface CommentsService {
    /**
     * 根据文章id查询所有评论列表
     * @param articleId
     * @return
     */
    Result commentsByArticleId(Long articleId);
}
