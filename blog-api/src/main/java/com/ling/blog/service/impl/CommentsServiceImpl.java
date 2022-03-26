package com.ling.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ling.blog.dao.mapper.CommentMapper;
import com.ling.blog.dao.pojo.Comment;
import com.ling.blog.dao.pojo.SysUser;
import com.ling.blog.service.CommentsService;
import com.ling.blog.service.SysUserService;
import com.ling.blog.utils.UserThreadLocal;
import com.ling.blog.vo.CommentVo;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.UserVo;
import com.ling.blog.vo.params.CommentParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentsServiceImpl implements CommentsService {
    private final CommentMapper commentMapper;

    private SysUserService sysUserService;

    @Autowired
    public CommentsServiceImpl(CommentMapper commentMapper,SysUserService sysUserService){
        this.commentMapper = commentMapper;
        this.sysUserService = sysUserService;
    }
    /**
     * 根据文章id查询所有评论列表
     * @param articleId
     * @return
     */
    @Override
    public Result commentsByArticleId(Long articleId) {
        /**
         * 1、根据文章id查询评论列表，从comments表中查询
         * 2、根据作者id查询作者信息
         * 3、判断如果level = 1; 要去查询有没有子评论
         * 4、如果有，根据评论id进行查询 (parent_id)
         */
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,articleId);
        //只查询level为1的评论，即给文章的评论，子评论会在当前评论下相继查询出来
        queryWrapper.eq(Comment::getLevel,1);
        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList = copyList(commentList);
        return Result.success(commentVoList);
    }

    /**
     * 对文章进行评论
     * @param commentParam
     * @return
     */
    @Override
    public Result commentTo(CommentParam commentParam) {
        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setContent(commentParam.getContent());
        comment.setAuthorId(sysUser.getId());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParam.getParent();
        if(parent == null || parent == 0){
            comment.setLevel(1);
        }else{
            comment.setLevel(2);
        }
        comment.setParentId(parent==null? 0 : parent);
        Long toUserId = commentParam.getToUserId();
        comment.setToUid(toUserId==null? 0:toUserId);
        this.commentMapper.insert(comment);
        return Result.success(null);
    }

    private List<CommentVo> copyList(List<Comment> commentList) {
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setId(String.valueOf(comment.getId()));
        //作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
        //子评论
        //level =1 说明是直接给文章的评论
        Integer level = comment.getLevel();
        if(1 == level){
            Long id = comment.getId();
            List<CommentVo> commentVoList = findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //to User 给谁评论的
        //level > 1 说明是给评论的评论
        if(level > 1){
            Long toUid = comment.getToUid();
            UserVo toUserVo = sysUserService.findUserVoById(authorId);
            commentVo.setAuthor(toUserVo);
        }
        return commentVo;
    }

    //子评论
    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        return copyList(commentMapper.selectList(queryWrapper));
    }
}
