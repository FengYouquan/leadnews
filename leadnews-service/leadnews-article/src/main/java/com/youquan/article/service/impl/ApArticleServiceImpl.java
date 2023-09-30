package com.youquan.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youquan.article.mapper.ApArticleMapper;
import com.youquan.article.service.ApArticleService;
import com.youquan.common.constant.ArticleConstant;
import com.youquan.model.article.dto.ArticleHomeDto;
import com.youquan.model.article.pojo.ApArticle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/30 22:16
 */
@RequiredArgsConstructor
@Service
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {
    private final ApArticleMapper apArticleMapper;

    /**
     * 加载文章
     *
     * @param articleHomeDto 加载条件
     * @param loadType       加载类型
     * @return List<ApArticle>
     */
    @Override
    public List<ApArticle> load(ArticleHomeDto articleHomeDto, Short loadType) {
        // 参数校验
        if (loadType == null || !Objects.equals(loadType, ArticleConstant.LOADTYPE_LOAD_MORE) && !Objects.equals(loadType, ArticleConstant.LOADTYPE_LOAD_NEW)) {
            loadType = ArticleConstant.LOADTYPE_LOAD_MORE;
        }
        return apArticleMapper.load(articleHomeDto, loadType);
    }
}
