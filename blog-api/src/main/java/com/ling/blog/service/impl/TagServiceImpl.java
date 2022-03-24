package com.ling.blog.service.impl;

import com.ling.blog.dao.mapper.TagMapper;
import com.ling.blog.dao.pojo.Tag;
import com.ling.blog.service.TagService;
import com.ling.blog.vo.Result;
import com.ling.blog.vo.TagVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;

    @Autowired
    public TagServiceImpl(TagMapper tagMapper){
        this.tagMapper = tagMapper;
    }
    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        //mybatis-plus无法进行多表查询
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);
        return copyTagVoList(tags);
    }

    @Override
    public Result hots(int limit) {
        /**
         * 1、标签所拥有的文章数量最多
         * 2、查询，根据tag_id分组，计数，从大到小，取前limit个
         */
        List<Long> tagIds = tagMapper.findHotsTagIds(limit);
        if(CollectionUtils.isEmpty(tagIds)){
            return Result.success(Collections.emptyList());
        }
        //需求的是tagId 和 tagName
        List<Tag> tagList = tagMapper.findTagsByTagIds(tagIds);
        return Result.success(tagList);
    }

    public List<TagVo> copyTagVoList(List<Tag> tags){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tags) {
            tagVoList.add(copyTagVo(tag));
        }
        return tagVoList;

    }
    public TagVo copyTagVo(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
}
