package com.ling.blog.controller;


import com.ling.blog.common.aop.LogAnnotation;
import com.ling.blog.common.cache.Cache;
import com.ling.blog.service.ArticleService;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.params.ArticleParam;
import com.ling.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//json数据进行交互
@RestController
@RequestMapping("/articles")
public class ArticleController {

    private ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService){
        this.articleService  = articleService;
    }
    /**
     * 首页文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    //加上此注解，代表要对此接口记录日志
    @LogAnnotation(module = "文章",operator = "获取文章列表")
    @Cache(expire = 5*60*1000,name = "listArticle")
    public Result listArticle(@RequestBody PageParams pageParams){

        return articleService.listArticle(pageParams);
    }

    /**
     * 最热文章列表
     * @return
     */
    @PostMapping("/hot")
    @Cache(expire = 5*60*1000,name ="hotArticle")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 最新文章列表
     * @return
     */
    @PostMapping("/new")
    @Cache(expire = 5*60*1000,name ="newArticle")
    public Result newArticle(){
        int limit = 5;
        return articleService.newArticle(limit);
    }

    /**
     * 文章归档
     * @return
     */
    @PostMapping("/listArchives")
    @Cache(expire = 5*60*1000,name ="listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    /**
     * 显示文章详情
     * @param articleId
     * @return
     */
    @PostMapping("/view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

    @PostMapping("/publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publishArticle(articleParam);
    }
}
