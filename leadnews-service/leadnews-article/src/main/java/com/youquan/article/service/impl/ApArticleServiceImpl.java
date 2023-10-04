package com.youquan.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youquan.article.mapper.ApArticleConfigMapper;
import com.youquan.article.mapper.ApArticleContentMapper;
import com.youquan.article.mapper.ApArticleMapper;
import com.youquan.article.service.ApArticleService;
import com.youquan.article.service.ArticleFreemarkerService;
import com.youquan.common.constant.ArticleConstant;
import com.youquan.common.exception.CustomException;
import com.youquan.model.article.dto.ArticleDto;
import com.youquan.model.article.dto.ArticleHomeDto;
import com.youquan.model.article.pojo.ApArticle;
import com.youquan.model.article.pojo.ApArticleConfig;
import com.youquan.model.article.pojo.ApArticleContent;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ApArticleConfigMapper apArticleConfigMapper;
    private final ApArticleContentMapper apArticleContentMapper;
    private final ArticleFreemarkerService articleFreemarkerService;

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

    /**
     * 保存或修改文章信息
     *
     * @param articleDto 文章参数
     * @return ResponseResult
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveOrUpdateArticle(ArticleDto articleDto) {
        // 检查参数
        if (articleDto == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(articleDto, apArticle);

        if (articleDto.getId() == null) {
            // 保存文章
            this.save(apArticle);

            // 保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            // 保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(articleDto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            // 修改文章
            updateById(apArticle);

            // 修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(
                    Wrappers.<ApArticleContent>lambdaQuery()
                            .eq(ApArticleContent::getArticleId, apArticle.getId()));
            apArticleContent.setContent(articleDto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        // 异步调用 生成静态文件上传到minio中
        articleFreemarkerService.buildArticleToMinIo(apArticle, articleDto.getContent());

        // 返回文章ID
        return apArticle.getId();
    }
}
