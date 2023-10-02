package com.youquan.wemedia.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youquan.model.wemedia.dto.WmMaterialDto;
import com.youquan.model.wemedia.pojo.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 0:47
 */
public interface WmMaterialService extends IService<WmMaterial> {
    /**
     * 自媒体端图片上传
     *
     * @param multipartFile 图片信息
     * @return WmMaterial
     */
    WmMaterial uploadPicture(MultipartFile multipartFile);

    /**
     * 自媒体端查询图片素材列表
     *
     * @param wmMaterialDto 查询参数
     * @return IPage <WmMaterial>
     */
    IPage<WmMaterial> listByUserId(WmMaterialDto wmMaterialDto);

    /**
     * 自媒体端素材收藏
     *
     * @param id 素材ID
     */
    void collect(Integer id);

    /**
     * 自媒体端素材取消收藏
     *
     * @param id 素材ID
     */
    void cancelCollect(Integer id);

    /**
     * 自媒体端删除素材
     *
     * @param id 素材ID
     */
    void deletePicture(Integer id);
}
