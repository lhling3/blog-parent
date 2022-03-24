package com.ling.blog.vo.params;

import lombok.Data;

@Data
public class CommentParam {
    private long articleId;
    private String content;
    private long parent;
    private long toUserId;
}
