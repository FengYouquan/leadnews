package com.youquan.wemedia.service;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 21:43
 */
public interface WmNewsAutoScanService {
    /**
     * 自媒体文章审核
     *
     * @param id 文章ID
     */
    void autoScanWmNews(Integer id);
}
