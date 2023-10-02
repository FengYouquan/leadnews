package com.youquan.wemedia.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youquan.model.wemedia.dto.WmNewsDto;
import com.youquan.model.wemedia.dto.WmNewsPageReqDto;
import com.youquan.model.wemedia.pojo.WmNews;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 12:27
 */
public interface WmNewsService extends IService<WmNews> {
    /**
     * 查询文章列表
     *
     * @param wmNewsPageReqDto 查询条件
     * @return IPage <WmNews>
     */
    IPage<WmNews> listByUserId(WmNewsPageReqDto wmNewsPageReqDto);

    /**
     * 自媒体端提交文章
     *
     * @param wmNewsDto 参数
     */
    void submitNews(WmNewsDto wmNewsDto);
}
