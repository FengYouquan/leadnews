package com.youquan.wemedia.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youquan.common.constant.WeMediaConstants;
import com.youquan.common.exception.CustomException;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.model.wemedia.dto.WmNewsDto;
import com.youquan.model.wemedia.dto.WmNewsPageReqDto;
import com.youquan.model.wemedia.pojo.WmMaterial;
import com.youquan.model.wemedia.pojo.WmNews;
import com.youquan.model.wemedia.pojo.WmNewsMaterial;
import com.youquan.model.wemedia.pojo.WmUser;
import com.youquan.utils.thread.WmThreadLocalUtils;
import com.youquan.wemedia.mapper.WmMaterialMapper;
import com.youquan.wemedia.mapper.WmNewsMapper;
import com.youquan.wemedia.mapper.WmNewsMaterialMapper;
import com.youquan.wemedia.service.WmNewsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 12:28
 */
@RequiredArgsConstructor
@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    private final WmNewsMaterialMapper wmNewsMaterialMapper;
    private final WmMaterialMapper wmMaterialMapper;
    private final WmNewsMapper wmNewsMapper;

    /**
     * 查询文章列表
     *
     * @param wmNewsPageReqDto 查询条件
     * @return IPage <WmNews>
     */
    @Override
    public IPage<WmNews> listByUserId(WmNewsPageReqDto wmNewsPageReqDto) {
        wmNewsPageReqDto.checkParam();

        // 获取当前登录人的信息
        WmUser wmUser = WmThreadLocalUtils.getUser();
        if (wmUser == null || wmUser.getId() == null) {
            throw new CustomException(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 分页条件查询
        IPage<WmNews> wmNewsPage = new Page<>(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize());

        // 构建请求条件
        LambdaQueryWrapper<WmNews> wmNewsLambdaQueryWrapper = new LambdaQueryWrapper<>();

        // 状态精确查询
        wmNewsLambdaQueryWrapper.eq(wmNewsPageReqDto.getStatus() != null, WmNews::getStatus, wmNewsPageReqDto.getStatus());

        // 频道精确查询
        wmNewsLambdaQueryWrapper.eq(wmNewsPageReqDto.getChannelId() != null, WmNews::getChannelId, wmNewsPageReqDto.getChannelId());

        // 时间范围查询
        wmNewsLambdaQueryWrapper.ge(wmNewsPageReqDto.getBeginPubDate() != null, WmNews::getPublishTime, wmNewsPageReqDto.getBeginPubDate());
        wmNewsLambdaQueryWrapper.le(wmNewsPageReqDto.getEndPubDate() != null, WmNews::getPublishTime, wmNewsPageReqDto.getEndPubDate());

        // 关键字模糊查询
        wmNewsLambdaQueryWrapper.like(wmNewsPageReqDto.getKeyword() != null && !wmNewsPageReqDto.getKeyword().isBlank(), WmNews::getTitle, wmNewsPageReqDto.getKeyword());

        // 查询当前登录用户的文章
        wmNewsLambdaQueryWrapper.eq(WmNews::getUserId, wmUser.getId());

        // 发布时间倒序查询
        wmNewsLambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);

        // 执行查询操作
        page(wmNewsPage, wmNewsLambdaQueryWrapper);

        return wmNewsPage;
    }

    /**
     * 自媒体端提交文章
     *
     * @param wmNewsDto 参数
     */
    @Override
    public void submitNews(WmNewsDto wmNewsDto) {
        // 参数校验
        if (wmNewsDto == null || wmNewsDto.getContent().isBlank()) {
            throw new CustomException(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        // 保存或修改文章
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(wmNewsDto, wmNews);

        // 处理封面图片
        if (wmNewsDto.getImages() != null && !wmNewsDto.getImages().isEmpty()) {
            String imageStr = StringUtils.join(wmNewsDto.getImages(), ",");
            wmNews.setImages(imageStr);
        }

        // 如果当前封面类型为自动 -1
        if (Objects.equals(wmNewsDto.getType(), WeMediaConstants.WM_NEWS_TYPE_AUTO)) {
            wmNews.setType(null);
        }

        this.saveOrUpdateWmNews(wmNews);

        // 判断是否为草稿,如果为草稿结束当前方法
        if (Objects.equals(wmNewsDto.getStatus(), WmNews.Status.NORMAL.getCode())) {
            return;
        }

        // 提取文章内容的图片数据
        List<String> imageList = extractUrlInfo(wmNewsDto.getContent());

        // 保存文章内容和图片素材的关联关系
        this.saveRelationshipOfNewsAndMaterial(imageList, wmNews.getId());

        // 保存文章封面和图片素材的关联关系
        this.saveRelationshipOfCoverAndMaterial(wmNewsDto.getImages(), wmNews, imageList);
    }

    private void saveRelationshipOfCoverAndMaterial(List<String> coverImageList, WmNews wmNews, List<String> imageList) {
        // 如果当前封面为自动，则设置封面类型的数据
        if (wmNews.getType() == null) {
            // 多图
            if (imageList != null && imageList.size() >= 3) {
                wmNews.setType(WeMediaConstants.WM_NEWS_MANY_IMAGE);
                coverImageList = imageList.stream().limit(3L).collect(Collectors.toList());
            } else if (imageList != null && !imageList.isEmpty()) {
                // 单图
                wmNews.setType(WeMediaConstants.WM_NEWS_SINGLE_IMAGE);
                coverImageList = imageList.stream().limit(1L).collect(Collectors.toList());
            } else {
                //  无图
                wmNews.setType(WeMediaConstants.WM_NEWS_NONE_IMAGE);
            }
            // 将封面图片数据添加到图文对象
            if (coverImageList != null && !coverImageList.isEmpty()) {
                wmNews.setImages(StringUtils.join(coverImageList, ","));
            }
            wmNewsMapper.updateById(wmNews);
        }

        if (coverImageList != null && !coverImageList.isEmpty()) {
            this.saveRelationship(coverImageList, wmNews.getId(), WeMediaConstants.WM_COVER_REFERENCE);
        }
    }

    /**
     * 保存图文内容和素材的关联关系
     *
     * @param imageList 素材URL集合
     * @param id        图文ID
     */
    private void saveRelationshipOfNewsAndMaterial(List<String> imageList, Integer id) {
        this.saveRelationship(imageList, id, WeMediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 保存图文元素和素材的关联关系
     *
     * @param imageList 素材URL集合
     * @param newsId    图文ID
     * @param type      类型
     */
    private void saveRelationship(List<String> imageList, Integer newsId, Short type) {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }

        // 去重处理
        imageList = imageList.stream().distinct().collect(Collectors.toList());
        // 通过素材URL查找素材信息
        List<WmMaterial> wmMaterialList = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, imageList));
        // 构建素材的ID集合
        List<Integer> wmMaterialIdList = wmMaterialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
        // 批量保存
        wmNewsMaterialMapper.insertRelationship(wmMaterialIdList, newsId, type);
    }

    /**
     * 提取文章中的图片
     *
     * @param content 文章内容
     * @return List <String> 图片URL集合
     */
    private List<String> extractUrlInfo(String content) {
        JSONArray contentJsonArray = JSON.parseArray(content);
        ArrayList<String> imageList = new ArrayList<>();
        contentJsonArray.forEach(contentJson -> {
            JSONObject jsonObject = JSONObject.parseObject(contentJson.toString());
            if (Objects.equals(jsonObject.getString("type"), "image")) {
                imageList.add(jsonObject.getString("value"));
            }
        });
        return imageList;
    }

    /**
     * 保存或更新新闻数据
     *
     * @param wmNews 新闻数据
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short) 1);

        if (wmNews.getId() == null) {
            this.save(wmNews);
        } else {
            // 删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery()
                    .eq(WmNewsMaterial::getNewsId, wmNews.getId()));
            this.updateById(wmNews);
        }
    }
}
