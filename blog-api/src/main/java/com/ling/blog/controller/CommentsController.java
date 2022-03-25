package com.ling.blog.controller;

import com.ling.blog.service.CommentsService;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentsController {
    private CommentsService commentsService;

    @Autowired
    public CommentsController(CommentsService commentsService){
        this.commentsService = commentsService;
    }

    /**
     * 显示文章评论
     * @param articleId
     * @return
     */
    @GetMapping("/article/{id}")
    public Result comments(@PathVariable("id") Long articleId){
    return commentsService.commentsByArticleId(articleId);
    }

    /**
     * 对文章评论
     */
    @PostMapping("/create/change")
    public Result commentTo(@RequestBody CommentParam commentParam){
        return commentsService.commentTo(commentParam);
    }

}
