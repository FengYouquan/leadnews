package com.youquan.model.article.dto;

import com.youquan.model.article.pojo.ApArticle;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 21:18
 */
@Getter
@Setter
public class ArticleDto extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}
