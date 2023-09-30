package com.youquan.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youquan.model.article.dto.ArticleHomeDto;
import com.youquan.model.article.pojo.ApArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/30 22:16
 */
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /**
     * 加载文章
     *
     * @param articleHomeDto 加载条件
     * @param loadType       加载类型
     * @return  List<ApArticle>
     */
    List<ApArticle> load(ArticleHomeDto articleHomeDto, Short loadType);
}
