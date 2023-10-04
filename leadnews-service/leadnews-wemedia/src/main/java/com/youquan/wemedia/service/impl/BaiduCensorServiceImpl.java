package com.youquan.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.youquan.common.exception.CustomException;
import com.youquan.common.redis.CacheService;
import com.youquan.file.service.FileStorageService;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.utils.baidu.Base64Util;
import com.youquan.utils.baidu.HttpUtil;
import com.youquan.wemedia.service.BaiduCensorService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 22:39
 */
@Getter
@Setter
@Service
@Slf4j
@ConfigurationProperties(prefix = "baidu.censor")
public class BaiduCensorServiceImpl implements BaiduCensorService {
    private String textCensorUrl;
    private String imageCensorUrl;
    private String clientId;
    private String clientSecret;
    private String tokenUrl;

    private final CacheService cacheService;
    private final FileStorageService fileStorageService;

    public BaiduCensorServiceImpl(CacheService cacheService, FileStorageService fileStorageService) {
        this.cacheService = cacheService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * 百度文本内容审核
     *
     * @param content 文本内容
     * @return 审核结果 Map<String, Object>
     */
    @Override
    public Map<String, Object> textCensor(String content) {
        try {
            String param = "text=" + URLEncoder.encode(content, "utf-8");

            // 获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = cacheService.get("leadnews:baidu:token");
            if (StringUtils.isBlank(accessToken)) {
                accessToken = this.getToken();
                cacheService.setEx("leadnews:baidu:token", accessToken, 180, TimeUnit.MINUTES);
            }
            String result = HttpUtil.post(textCensorUrl, accessToken, param);

            return parseCensorResponse(result);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }


    /**
     * 百度图像内容审核
     *
     * @param pathUrl 本地图像路径
     * @return 审核结果 Map<String, Object>
     */
    @Override
    public Map<String, Object> imageCensor(String pathUrl) {
        try {
            byte[] imgData = fileStorageService.downLoadFile(pathUrl);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;

            // 获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = cacheService.get("leadnews:baidu:token");
            if (StringUtils.isBlank(accessToken)) {
                accessToken = this.getToken();
                cacheService.setEx("leadnews:baidu:token", accessToken, 180, TimeUnit.MINUTES);
            }

            String result = HttpUtil.post(imageCensorUrl, accessToken, param);

            return parseCensorResponse(result);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 解析百度内容审核的响应结果
     *
     * @param result 响应结果
     * @return HashMap<String, Object>
     */
    private static HashMap<String, Object> parseCensorResponse(String result) {
        if (StringUtils.isBlank(result)) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", jsonObject.getInteger("conclusionType"));
        ArrayList<String> msgList;
        String data = jsonObject.getString("data");
        if (StringUtils.isNotBlank(data)) {
            msgList = new ArrayList<>();
            JSONArray jsonArray = JSONArray.parseArray(data);
            jsonArray.forEach(json -> {
                JSONObject jsonObject2 = JSONObject.parseObject(json.toString());
                if (jsonObject2 != null) {
                    String msg = jsonObject2.getString("msg");
                    msgList.add(msg);
                }
            });
            resultMap.put("msg", msgList);
        }
        return resultMap;
    }

    /**
     * 获取访问Token
     *
     * @return String Token
     */
    private String getToken() {
        String url = this.tokenUrl + "?client_id=".concat(clientId) + "&client_secret=".concat(clientSecret) + "&grant_type=client_credentials";
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url(url).method("POST", body).addHeader("Content-Type", "application/json").addHeader("Accept", "application/json").build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            String token = null;
            if (response.body() != null) {
                JSONObject jsonObject = JSONObject.parseObject(response.body().string());
                token = jsonObject.getString("access_token");
            }
            return token;
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
    }
}
