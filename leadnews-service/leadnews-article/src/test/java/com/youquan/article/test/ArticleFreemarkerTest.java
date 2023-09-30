package com.youquan.article.test;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youquan.article.ArticleApplication;
import com.youquan.article.mapper.ApArticleContentMapper;
import com.youquan.article.mapper.ApArticleMapper;
import com.youquan.file.service.FileStorageService;
import com.youquan.model.article.pojo.ApArticle;
import com.youquan.model.article.pojo.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.HashMap;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 0:28
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {
    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Test
    public void createStaticUrlTest() throws Exception {
        // 1.获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(
                Wrappers.<ApArticleContent>lambdaQuery()
                        .eq(ApArticleContent::getArticleId, 1383827787629252610L));

        if (apArticleContent == null || StringUtils.isBlank(apArticleContent.getContent())) {
            return;
        }

        // 2.文章内容通过freemarker生成html文件
        StringWriter stringWriter = new StringWriter();
        Template template = configuration.getTemplate("article.ftl");

        HashMap<String, Object> params = new HashMap<>();
        params.put("content", JSONArray.parseArray(apArticleContent.getContent()));

        template.process(params, stringWriter);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(stringWriter.toString().getBytes());

        // 3.把html文件上传到minio中
        String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", byteArrayInputStream);

        // 4.修改ap_article表，保存static_url字段
        ApArticle apArticle = new ApArticle();
        apArticle.setId(apArticleContent.getArticleId());
        apArticle.setStaticUrl(path);
        apArticleMapper.updateById(apArticle);
    }
}
