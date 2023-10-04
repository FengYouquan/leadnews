package com.youquan.article.feign;

import com.youquan.api.article.IArticleClient;
import com.youquan.article.service.ApArticleService;
import com.youquan.model.article.dto.ArticleDto;
import com.youquan.model.common.dto.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 21:23
 */
@RequiredArgsConstructor
@Slf4j
@RestController
public class ArticleClient implements IArticleClient {
    private final ApArticleService apArticleService;

    /**
     * 保存或修改文章信息
     *
     * @param articleDto 文章参数
     * @return ResponseResult
     */
    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult<?> saveOrUpdateArticle(@RequestBody ArticleDto articleDto) {
        log.info("保存或修改文章信息,{}", articleDto);
        return ResponseResult.okResult(apArticleService.saveOrUpdateArticle(articleDto));
    }
}
