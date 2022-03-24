package com.ling.blog.service;

import com.ling.blog.vo.Result;
import com.ling.blog.vo.params.PageParams;
import org.springframework.web.bind.annotation.RequestBody;

public interface ArticleService {
    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 首页，最热文章
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 首页，最新文章
     * @param limit
     * @return
     */
    Result newArticle(int limit);

    /**
     * 文章归档
     * @return
     */
    Result listArchives();

    /**
     * 查看文章详情
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);
}
