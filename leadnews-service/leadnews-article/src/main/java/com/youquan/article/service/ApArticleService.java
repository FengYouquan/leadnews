package com.youquan.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youquan.model.article.dto.ArticleDto;
import com.youquan.model.article.dto.ArticleHomeDto;
import com.youquan.model.article.pojo.ApArticle;

import java.util.List;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/30 22:15
 */
public interface ApArticleService extends IService<ApArticle> {
    /**
     * 加载文章
     *
     * @param articleHomeDto 加载条件
     * @param loadType       加载类型
     * @return List <ApArticle>
     */
    List<ApArticle> load(ArticleHomeDto articleHomeDto, Short loadType);

    /**
     * 保存或修改文章信息
     *
     * @param articleDto 文章参数
     * @return ResponseResult
     */
    Long saveOrUpdateArticle(ArticleDto articleDto);
}
