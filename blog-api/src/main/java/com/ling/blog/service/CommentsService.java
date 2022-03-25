package com.ling.blog.service;

import com.ling.blog.vo.Result;
import com.ling.blog.vo.params.CommentParam;

public interface CommentsService {
    /**
     * 根据文章id查询所有评论列表
     * @param articleId
     * @return
     */
    Result commentsByArticleId(Long articleId);

    /**
     * 对文章进行评论
     * @param commentParam
     * @return
     */
    Result commentTo(CommentParam commentParam);
}
