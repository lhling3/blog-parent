package com.ling.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ling.blog.dao.dos.Archives;
import com.ling.blog.dao.mapper.ArticleBodyMapper;
import com.ling.blog.dao.mapper.ArticleMapper;
import com.ling.blog.dao.mapper.ArticleTagMapper;
import com.ling.blog.dao.mapper.TagMapper;
import com.ling.blog.dao.pojo.*;
import com.ling.blog.service.*;
import com.ling.blog.utils.UserThreadLocal;
import com.ling.blog.vo.ArticleBodyVo;
import com.ling.blog.vo.ArticleVo;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.TagVo;
import com.ling.blog.vo.params.ArticleParam;
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

    private final ArticleTagMapper articleTagMapper;
    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper,TagService tagService,
                              SysUserService sysUserService,ArticleBodyMapper articleBodyMapper,
                              CategoryService categoryService,ThreadService threadService,
                              ArticleTagMapper articleTagMapper){
        this.articleMapper = articleMapper;
        this.tagService = tagService;
        this.sysUserService = sysUserService;
        this.articleBodyMapper = articleBodyMapper;
        this.categoryService = categoryService;
        this.threadService = threadService;
        this.articleTagMapper = articleTagMapper;
    }

    /**
     * ????????????
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
     * ????????????
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
     * ????????????
     *
     * @return
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    /**
     * ??????????????????
     * @param articleId
     * @return
     */
    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1?????????id??????????????????
         * 2?????????bodyId???categoryid ??????????????????
         */
        Article article = articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true,true,true);
        //?????????????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //????????????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????????????????????????????????????????????????????
        threadService.updateArticleViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    /**
     * ????????????
     * @param articleParam
     * @return
     */
    @Override
    public Result publishArticle(ArticleParam articleParam) {
        /**
         * 1?????????????????????????????????Article??????
         * 2?????????id?????????????????????
         * 3????????? ????????????????????????????????????
         */
        SysUser sysUser = UserThreadLocal.get();

        Article article = new Article();
        article.setAuthorId(sysUser.getId());
        article.setTitle(articleParam.getTitle());
        article.setCreateDate(System.currentTimeMillis());
        article.setCategoryId(Long.parseLong(articleParam.getCategory().getId()));
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        article.setBodyId(-1L);
        articleMapper.insert(article);

        List<TagVo> tags = articleParam.getTags();
        if(tags != null){
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setArticleId(article.getId());
                articleTag.setTagId(Long.parseLong(tag.getId()));
                this.articleTagMapper.insert(articleTag);
            }
        }
        ArticleBody articleBody = new ArticleBody();
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        this.articleBodyMapper.insert(articleBody);

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(String.valueOf(article.getId()));
        //?????????map???????????????????????????????????????
        return Result.success(articleVo);
    }

    /**
     * ????????????
     * @param pageParams
     * @return
     */
    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(page,pageParams.getCategoryId(),
                                        pageParams.getTagId(),pageParams.getYear(),
                                        pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        return  Result.success(copyList(records,true,true));
    }
    /*@Override
    public Result listArticle(PageParams pageParams) {
        *//**
         * 1???????????????article????????????
         *//*
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        if(pageParams.getCategoryId() != null){
            queryWrapper.eq(Article::getCategoryId,pageParams.getCategoryId());
        }
        List<Long> articleIdList = new ArrayList<>();
        if(pageParams.getTagId() != null){
            //????????????????????????
            //article?????????tag??????????????????????????????????????????
            //article_tag???????????????????????? article_id : tag_id
            LambdaQueryWrapper<ArticleTag> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(ArticleTag::getTagId,pageParams.getTagId());
            List<ArticleTag> articleTags = articleTagMapper.selectList(queryWrapper1);
            for (ArticleTag articleTag : articleTags) {
                articleIdList.add(articleTag.getArticleId());
            }
            if(articleIdList.size() > 0){
                //and id in()
                queryWrapper.in(Article::getId,articleIdList);
            }
        }
        //??????????????????
        //????????????????????????
        queryWrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = articlePage.getRecords();
        List<ArticleVo> articleVoList = copyList(records,true,true);
        return Result.success(articleVoList);

    }*/

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
        articleVo.setId(String.valueOf(article.getId()));
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
     * ????????????id??????????????????
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
