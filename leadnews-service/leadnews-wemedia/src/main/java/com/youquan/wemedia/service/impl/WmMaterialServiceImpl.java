package com.youquan.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youquan.common.exception.CustomException;
import com.youquan.file.service.FileStorageService;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.model.wemedia.dto.WmMaterialDto;
import com.youquan.model.wemedia.pojo.WmMaterial;
import com.youquan.model.wemedia.pojo.WmNewsMaterial;
import com.youquan.utils.thread.WmThreadLocalUtils;
import com.youquan.wemedia.mapper.WmMaterialMapper;
import com.youquan.wemedia.mapper.WmNewsMaterialMapper;
import com.youquan.wemedia.service.WmMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 0:47
 */
@RequiredArgsConstructor
@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {
    private final FileStorageService fileStorageService;
    private final WmNewsMaterialMapper wmNewsMaterialMapper;

    /**
     * 自媒体端图片上传
     *
     * @param multipartFile 图片信息
     * @return WmMaterial
     */
    @Override
    public WmMaterial uploadPicture(MultipartFile multipartFile) {
        // 参数校验
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 上传图片到minIO中
        String fileName = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
        } catch (IOException e) {
            log.error("WmMaterialServiceImpl-上传文件失败", e);
        }

        // 保存到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtils.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setType((short) 0);
        wmMaterial.setCreatedTime(new Date());
        this.save(wmMaterial);

        return wmMaterial;
    }

    /**
     * 自媒体端查询图片素材列表
     *
     * @param wmMaterialDto 查询参数
     * @return IPage <WmMaterial>
     */
    @Override
    public IPage<WmMaterial> listByUserId(WmMaterialDto wmMaterialDto) {
        // 参数校验
        if (Objects.isNull(wmMaterialDto)) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 数据处理
        wmMaterialDto.checkParam();

        // 构建请求条件
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = Wrappers.<WmMaterial>lambdaQuery()
                .eq(
                        wmMaterialDto.getIsCollection() != null && Objects.equals(wmMaterialDto.getIsCollection(), (short) 1),
                        WmMaterial::getIsCollection,
                        wmMaterialDto.getIsCollection()
                ).eq(WmMaterial::getUserId, WmThreadLocalUtils.getUser().getId())
                .orderByDesc(WmMaterial::getCreatedTime);

        // 执行查询
        IPage<WmMaterial> wmMaterialPage = new Page<>();
        this.page(wmMaterialPage, lambdaQueryWrapper);
        return wmMaterialPage;
    }

    /**
     * 自媒体端素材收藏
     *
     * @param id 素材ID
     */
    @Override
    public void collect(Integer id) {
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setId(id);
        wmMaterial.setIsCollection((short) 1);
        this.updateById(wmMaterial);
    }

    /**
     * 自媒体端素材取消收藏
     *
     * @param id 素材ID
     */
    @Override
    public void cancelCollect(Integer id) {
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setId(id);
        wmMaterial.setIsCollection((short) 0);
        this.updateById(wmMaterial);
    }

    /**
     * 自媒体端删除素材
     *
     * @param id 素材ID
     */
    @Override
    public void deletePicture(Integer id) {
        // 判断素材是否被引用
        Integer count = wmNewsMaterialMapper.selectCount(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getMaterialId, id));
        if (count > 0) {
            throw new CustomException(AppHttpCodeEnum.NO_OPERATOR_AUTH);
        }
        WmMaterial wmMaterial = this.getById(id);
        // 删除数MinIO中的素材
        fileStorageService.delete(wmMaterial.getUrl());
        // 删除数据库中的素材
        this.removeById(id);
    }
}
