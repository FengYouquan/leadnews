package com.youquan.wemedia.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.youquan.api.article.IArticleClient;
import com.youquan.common.exception.CustomException;
import com.youquan.common.redis.CacheService;
import com.youquan.common.tess4j.Tess4jClient;
import com.youquan.file.service.FileStorageService;
import com.youquan.model.article.dto.ArticleDto;
import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.model.wemedia.pojo.WmChannel;
import com.youquan.model.wemedia.pojo.WmNews;
import com.youquan.model.wemedia.pojo.WmSensitive;
import com.youquan.model.wemedia.pojo.WmUser;
import com.youquan.utils.common.SensitiveWordUtil;
import com.youquan.wemedia.mapper.WmChannelMapper;
import com.youquan.wemedia.mapper.WmNewsMapper;
import com.youquan.wemedia.mapper.WmSensitiveMapper;
import com.youquan.wemedia.mapper.WmUserMapper;
import com.youquan.wemedia.service.BaiduCensorService;
import com.youquan.wemedia.service.WmNewsAutoScanService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/4 21:46
 */
@RequiredArgsConstructor
@Service
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {
    private final WmNewsMapper wmNewsMapper;
    private final BaiduCensorService baiduCensorService;
    private final WmChannelMapper wmChannelMapper;
    private final WmUserMapper wmUserMapper;
    private final IArticleClient articleClient;
    private final WmSensitiveMapper wmSensitiveMapper;
    private final CacheService cacheService;
    private final Tess4jClient tess4jClient;
    private final FileStorageService fileStorageService;

    /**
     * 自媒体文章审核
     *
     * @param id 文章ID
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void autoScanWmNews(Integer id) {
        // 参数校验
        if (id == null) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 判断文章审核状态
        if (!Objects.equals(wmNews.getStatus(), WmNews.Status.SUBMIT.getCode())) {
            return;
        }

        // 从内容中提取文本内容和图片
        Map<String, Object> textAndImage = extractTextAndImages(wmNews);
        if (textAndImage.isEmpty()) {
            return;
        }

        // 审核文本内容 自定义接口和百度云接口
        String text = (String) textAndImage.get("plainText");
        if (StringUtils.isNotBlank(text)) {
            WmNews wmNews2 = new WmNews();
            wmNews2.setId(wmNews.getId());

            // 审核文本内容 自定义接口
            boolean result = myCensorService(text);
            if (!result) {
                wmNews2.setStatus((short) 2);
                wmNews2.setReason("文本内容违规，审核失败");
                wmNewsMapper.updateById(wmNews2);
                return;
            }

            // 审核文本内容 百度云接口
            Map<String, Object> textedCensor = baiduCensorService.textCensor(text);
            Integer code = (Integer) textedCensor.get("code");
            String reason;

            // 审核失败
            if (code == 2 || code == 4) {
                ArrayList<String> msgs = (ArrayList<String>) textedCensor.get("msg");
                reason = msgs != null ? String.join(",", msgs) : null;

                wmNews2.setStatus((short) 2);
                wmNews2.setReason(reason);
                wmNewsMapper.updateById(wmNews2);
                return;
            } else if (code == 3) {
                ArrayList<String> msgs = (ArrayList<String>) textedCensor.get("msg");
                reason = msgs != null ? String.join(",", msgs) : null;

                wmNews2.setStatus((short) 3);
                wmNews2.setReason(reason);
                wmNewsMapper.updateById(wmNews2);
                return;
            } else {
                wmNews2.setStatus((short) 8);
                wmNews2.setReason("审核成功");
                wmNewsMapper.updateById(wmNews2);
            }
        }

        // 审核图片信息 百度云接口
        ArrayList<String> imageList = (ArrayList<String>) textAndImage.get("imageList");
        if (imageList != null && !imageList.isEmpty()) {
            List<String> images = imageList.stream().distinct().collect(Collectors.toList());
            images.forEach(imagePath -> {
                Map<String, Object> imageCensor = baiduCensorService.imageCensor(imagePath);
                Integer code = (Integer) imageCensor.get("code");
                String reason;

                WmNews wmNews2 = new WmNews();
                wmNews2.setId(wmNews.getId());
                // 审核失败
                if (code == 2 || code == 4) {
                    ArrayList<String> msgs = (ArrayList<String>) imageCensor.get("msg");
                    reason = msgs != null ? String.join(",", msgs) : null;

                    wmNews2.setStatus((short) 2);
                    wmNews2.setReason(reason);
                    wmNewsMapper.updateById(wmNews2);
                    return;
                } else if (code == 3) {
                    ArrayList<String> msgs = (ArrayList<String>) imageCensor.get("msg");
                    reason = msgs != null ? String.join(",", msgs) : null;
                    wmNews2.setStatus((short) 3);
                    wmNews2.setReason(reason);
                    wmNewsMapper.updateById(wmNews2);
                    return;
                } else {
                    wmNews2.setStatus((short) 8);
                    wmNews2.setReason("审核成功");
                    wmNewsMapper.updateById(wmNews2);
                }
            });
        }

        // 审核成功，保存APP端的相关的文章数据
        Long articleId = saveAppArticle(wmNews);

        // 回填article_id
        WmNews wmNews2 = new WmNews();
        wmNews2.setId(wmNews.getId());
        wmNews2.setArticleId(articleId);
        wmNews2.setStatus(WmNews.Status.PUBLISHED.getCode());
        wmNews2.setReason("审核成功");
        wmNewsMapper.updateById(wmNews2);
    }

    private Long saveAppArticle(WmNews wmNews) {
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, articleDto);

        // 文章的布局
        articleDto.setLayout(wmNews.getType());
        // 频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            articleDto.setChannelName(wmChannel.getName());
        }
        // 作者
        articleDto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            articleDto.setAuthorName(wmUser.getName());
        }
        // 设置文章id
        if (wmNews.getArticleId() != null) {
            articleDto.setId(wmNews.getArticleId());
        }
        articleDto.setCreatedTime(new Date());

        ResponseResult<?> responseResult = articleClient.saveOrUpdateArticle(articleDto);
        if (!Objects.equals(responseResult.getCode(), AppHttpCodeEnum.SUCCESS.getCode())) {
            throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
        }
        return (Long) responseResult.getData();
    }

    private boolean myCensorService(String text) {
        // 获取所有的敏感词
        List<String> sensitiveList;
        String sensitiveCache = cacheService.get("leadnews:sensitive");
        if (StringUtils.isNotBlank(sensitiveCache)) {
            sensitiveList = JSON.parseObject(sensitiveCache, List.class);
        } else {
            List<WmSensitive> wmSensitiveList = wmSensitiveMapper.selectList(null);
            sensitiveList = wmSensitiveList.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
            cacheService.setEx("leadnews:sensitive", JSON.toJSONString(sensitiveList), 180, TimeUnit.MINUTES);
        }
        // 初始化敏感词库
        SensitiveWordUtil.initMap(sensitiveList);

        // 查看文章中是否包含敏感词
        Map<String, Integer> resultMap = SensitiveWordUtil.matchWords(text);
        return resultMap.isEmpty();
    }

    private Map<String, Object> extractTextAndImages(WmNews wmNews) {
        if (wmNews.getContent() == null || wmNews.getContent().isBlank()) {
            return Collections.emptyMap();
        }

        // 创建StringBuilder对象用于存储纯文本内容
        StringBuilder plainText = new StringBuilder();
        // 创建List集合对象用于存储图片信息
        ArrayList<String> imageList = new ArrayList<>();

        // 分析文章内容并提取纯文本和图片
        JSONArray jsonArray = JSON.parseArray(wmNews.getContent());
        jsonArray.forEach(json -> {
            JSONObject jsonObject = JSONObject.parseObject(json.toString());
            String type = jsonObject.getString("type");
            String value = jsonObject.getString("value");

            if (Objects.equals(type, "text")) {
                plainText.append(value);
            } else if (Objects.equals(type, "image")) {
                // 从byte[]转换为butteredImage
                byte[] imageBytes = fileStorageService.downLoadFile(value.trim());
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
                try {
                    BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                    String textInImage = tess4jClient.doOcr(bufferedImage);
                    plainText.append(textInImage);
                } catch (Exception e) {
                    throw new CustomException(AppHttpCodeEnum.SERVER_ERROR);
                }
                imageList.add(value);
            }
        });

        // 分析文章标题并提取纯文本
        if (StringUtils.isNotBlank(wmNews.getTitle())) {
            plainText.append(wmNews.getTitle());
        }

        // 分析文章标签并提取纯文本
        if (StringUtils.isNotBlank(wmNews.getLabels())) {
            plainText.append(wmNews.getLabels());
        }

        // 分析文章封面并提取图片
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] images = wmNews.getImages().split(",");
            imageList.addAll(Arrays.asList(images));
        }

        // 创建Map对象，用于存储结果数据
        HashMap<String, Object> result = new HashMap<>();
        result.put("plainText", plainText.toString());
        result.put("imageList", imageList);
        return result;
    }
}
