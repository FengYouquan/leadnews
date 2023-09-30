package com.youquan.article.controller.v1;

import com.youquan.article.service.ApArticleService;
import com.youquan.common.constant.ArticleConstant;
import com.youquan.model.article.dto.ArticleHomeDto;
import com.youquan.model.common.dto.ResponseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/30 22:07
 */
@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {
    private final ApArticleService apArticleService;

    /**
     * 加载文章
     *
     * @param articleHomeDto 加载条件
     * @return ResponseResult
     */
    @PostMapping("/load")
    public ResponseResult<?> load(@RequestBody ArticleHomeDto articleHomeDto) {
        log.info("加载文章，{}", articleHomeDto);
        return ResponseResult.okResult(apArticleService.load(articleHomeDto, ArticleConstant.LOADTYPE_LOAD_MORE));
    }

    /**
     * 加载更多文章
     *
     * @param articleHomeDto 加载条件
     * @return ResponseResult
     */
    @PostMapping("/loadmore")
    public ResponseResult<?> loadMore(@RequestBody ArticleHomeDto articleHomeDto) {
        log.info("加载更多文章，{}", articleHomeDto);
        return ResponseResult.okResult(apArticleService.load(articleHomeDto, ArticleConstant.LOADTYPE_LOAD_MORE));
    }

    /**
     * 加载最新文章
     *
     * @param articleHomeDto 加载条件
     * @return ResponseResult
     */
    @PostMapping("/loadnew")
    public ResponseResult<?> loadNew(@RequestBody ArticleHomeDto articleHomeDto) {
        log.info("加载最新文章，{}", articleHomeDto);
        return ResponseResult.okResult(apArticleService.load(articleHomeDto, ArticleConstant.LOADTYPE_LOAD_NEW));
    }
}
