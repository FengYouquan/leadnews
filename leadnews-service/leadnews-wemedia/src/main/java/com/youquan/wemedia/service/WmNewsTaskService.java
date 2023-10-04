package com.youquan.wemedia.service;

import java.util.Date;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/5 1:16
 */
public interface WmNewsTaskService {
    /**
     * 添加任务到任务队列
     *
     * @param newsId      文章ID
     * @param publishTime 文章发布时间
     */
    void addNewsToTask(Integer newsId, Date publishTime);

    /**
     * 从任务队列消费数据
     */
    void scanNewsByTask();
}
