package com.ling.blog.controller;

import com.ling.blog.service.TagService;
import com.ling.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
public class TagsController {
    private TagService tagService;

    @Autowired
    public TagsController(TagService tagService){
        this.tagService = tagService;
    }
    @GetMapping("/hot")
    public Result hot(){
        int limit = 6;
        return tagService.hots(limit);
    }

    /**
     * 列出所有文章标签
     * @return
     */
    @GetMapping
    public Result findAllTags(){
        return tagService.findAllTags();
    }

    @GetMapping("/detail")
    public Result tagsDetail(){
        return tagService.findAllDetail();
    }

    @GetMapping("/detail/{id}")
    public Result tagDetailById(@PathVariable("id") Long id){
        return tagService.tagDetailById(id);
    }
}
