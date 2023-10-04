package com.youquan.wemedia.service;

import java.util.Map;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 22:39
 */
public interface BaiduCensorService {
    /**
     * 百度文本内容审核
     *
     * @param content 文本内容
     * @return 审核结果
     */
    Map<String, Object> textCensor(String content);

    /**
     * 百度图像内容审核
     *
     * @param path 本地图像路径
     * @return 审核结果
     */
    Map<String, Object> imageCensor(String path);
}
