package com.youquan.article.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.youquan.article.mapper.ApArticleMapper;
import com.youquan.article.service.ArticleFreemarkerService;
import com.youquan.common.exception.CustomException;
import com.youquan.file.service.FileStorageService;
import com.youquan.model.article.pojo.ApArticle;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 23:58
 */
@Async
@RequiredArgsConstructor
@Service
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {
    private final Configuration configuration;
    private final FileStorageService fileStorageService;
    private final ApArticleMapper apArticleMapper;

    /**
     * 生成静态文件上传到minIO中
     *
     * @param apArticle 文章
     * @param content   文章内容
     */
    @Override
    public void buildArticleToMinIo(ApArticle apArticle, String content) {
        if (StringUtils.isBlank(content)) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 通过freemarker将文章内容生成html文件
        Template template;
        StringWriter stringWriter = new StringWriter();
        try {
            template = configuration.getTemplate("article.ftl");
            // 数据模型
            Map<String, Object> contentDataModel = new HashMap<>();
            contentDataModel.put("content", JSONArray.parseArray(content));
            // 合成
            template.process(contentDataModel, stringWriter);
        } catch (Exception e) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }

        // 把html文件上传到minio中
        InputStream in = new ByteArrayInputStream(stringWriter.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);

        // 修改ap_article表，保存static_url字段
        ApArticle apArticle2 = new ApArticle();
        apArticle2.setId(apArticle.getId());
        apArticle2.setStaticUrl(path);
        apArticleMapper.updateById(apArticle2);
    }
}
