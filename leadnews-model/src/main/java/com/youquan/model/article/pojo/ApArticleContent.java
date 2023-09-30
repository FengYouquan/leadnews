package com.youquan.model.article.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/30 21:52
 */
@Data
@TableName("ap_article_content")
public class ApArticleContent {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 文章内容
     */
    private String content;
}
