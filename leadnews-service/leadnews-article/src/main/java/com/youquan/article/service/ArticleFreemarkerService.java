package com.youquan.article.service;

import com.youquan.model.article.pojo.ApArticle;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 23:57
 */
public interface ArticleFreemarkerService {
    /**
     * 生成静态文件上传到minIO中
     *
     * @param apArticle 文章
     * @param content   文章内容
     */
    void buildArticleToMinIo(ApArticle apArticle, String content);
}
