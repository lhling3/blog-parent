package com.ling.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ling.blog.dao.dos.Archives;
import com.ling.blog.dao.mapper.ArticleBodyMapper;
import com.ling.blog.dao.mapper.ArticleMapper;
import com.ling.blog.dao.mapper.TagMapper;
import com.ling.blog.dao.pojo.Article;
import com.ling.blog.dao.pojo.ArticleBody;
import com.ling.blog.service.*;
import com.ling.blog.vo.ArticleBodyVo;
import com.ling.blog.vo.ArticleVo;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;

    private TagService tagService;

    private SysUserService sysUserService;

    private final ArticleBodyMapper articleBodyMapper;

    private CategoryService categoryService;

    private ThreadService threadService;
    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper,TagService tagService,
                              SysUserService sysUserService,ArticleBodyMapper articleBodyMapper,
                              CategoryService categoryService,ThreadService threadService){
        this.articleMapper = articleMapper;
        this.tagService = tagService;
        this.sysUserService = sysUserService;
        this.articleBodyMapper = articleBodyMapper;
        this.categoryService = categoryService;
        this.threadService = threadService;
    }

    /**
     * 最热文章
     * @param limit
     * @return
     */
    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //select id,title from ms_article order by view_counts desc limit 5
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    /**
     * 最新文章
     * @param limit
     * @return
     */
    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //select id,title from ms_article order by create_date desc limit 5
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.last("limit "+limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles,false,false));
    }

    /**
     * 文章归档
     *
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    /**
     * 查看文章详情
     * @param articleId
     * @return
     */
    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1、根据id查询文章信息
         * 2、根据bodyId和categoryid 去做关联查询
         */
        Article article = articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true,true,true);
        //查看完文章，新增阅读数
        //查看完文章，本应字节放回数据，如果此时做了更新操作，更新时会加写锁，阻塞其他的读操作，性能比较低
        //更新增加了此次接口的耗时，如更新出现问题，不能影响查看文章的操作
        //线程池，可以把更新操作扔到线程池中去执行，和主线程就不相关了
        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    /**
     * 文章列表
     * @param pageParams
     * @return
     */
    @Override
    public Result listArticle(PageParams pageParams) {
        /**
         * 1、分页查询article数据库表
         */
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //是否置顶排序
        //再按创建时间排序
        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = articlePage.getRecords();
        List<ArticleVo> articleVoList = copyList(records,true,true);
        return Result.success(articleVoList);

    }

    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            articleVoList.add(copy(article,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }
    private List<ArticleVo> copyList(List<Article> records,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            articleVoList.add(copy(article,isTag,isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }
    public ArticleVo copy(Article article,boolean isTag,boolean isAuthor,boolean isBody,boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        if(isTag){
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if(isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if(isCategory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    /**
     * 根据文章id查询文章内容
     * @param bodyId
     * @return
     */
   /* @Autowired
    private ArticleBodyMapper articleBodyMapper;*/
    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }
}
