package com.ling.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ling.blog.dao.pojo.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据文章id查询标签列表
     * @param articleId
     * @return
     */
    List<Tag> findTagsByArticleId(@Param("articleId") Long articleId);

    /**
     * 查询最热标签前n条
     * @param limit
     * @return
     */
    List<Long> findHotsTagIds(@Param("limit") int limit);

    /**
     * 根据tag_id查询tags
     * @param tagIds
     * @return
     */
    List<Tag> findTagsByTagIds(@Param("tagIds") List<Long> tagIds);
}