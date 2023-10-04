package com.youquan.api.article;

import com.youquan.model.article.dto.ArticleDto;
import com.youquan.model.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 21:20
 */
@FeignClient(value = "leadnews-article")
public interface IArticleClient {
    /**
     * 保存或修改文章信息
     *
     * @param articleDto 文章参数
     * @return ResponseResult
     */
    @PostMapping("/api/v1/article/save")
    ResponseResult<?> saveOrUpdateArticle(@RequestBody ArticleDto articleDto);
}
