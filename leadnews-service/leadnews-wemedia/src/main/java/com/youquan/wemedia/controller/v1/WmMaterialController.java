package com.youquan.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youquan.model.common.dto.PageResponseResult;
import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.model.wemedia.dto.WmMaterialDto;
import com.youquan.model.wemedia.pojo.WmMaterial;
import com.youquan.wemedia.service.WmMaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 0:44
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {
    private final WmMaterialService wmMaterialService;

    /**
     * 自媒体端素材上传
     *
     * @param multipartFile 图片信息
     * @return ResponseResult
     */
    @PostMapping("/upload_picture")
    public ResponseResult<?> uploadPicture(MultipartFile multipartFile) {
        log.info("自媒体端素材上传，{}", multipartFile);
        return ResponseResult.okResult(wmMaterialService.uploadPicture(multipartFile));
    }

    /**
     * 自媒体端查询素材列表
     *
     * @param wmMaterialDto 查询参数
     * @return ResponseResult
     */
    @PostMapping("/list")
    public ResponseResult<?> list(@RequestBody WmMaterialDto wmMaterialDto) {
        log.info("自媒体端查询素材列表，{}", wmMaterialDto);
        IPage<WmMaterial> wmMaterialPage = wmMaterialService.listByUserId(wmMaterialDto);

        PageResponseResult pageResponseResult = new PageResponseResult(
                (int) wmMaterialPage.getCurrent(),
                (int) wmMaterialPage.getSize(),
                (int) wmMaterialPage.getTotal()
        );
        return pageResponseResult.ok(AppHttpCodeEnum.SUCCESS.getCode(), wmMaterialPage.getRecords());
    }

    /**
     * 自媒体端素材收藏
     *
     * @param id 素材ID
     * @return ResponseResult
     */
    @GetMapping("/collect/{id}")
    public ResponseResult<?> collect(@PathVariable @NotNull Integer id) {
        log.info("自媒体端素材收藏，{}", id);
        wmMaterialService.collect(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 自媒体端素材取消收藏
     *
     * @param id 素材ID
     * @return ResponseResult
     */
    @GetMapping("/cancel_collect/{id}")
    public ResponseResult<?> cancelCollect(@PathVariable @NotNull Integer id) {
        log.info("自媒体端素材取消收藏，{}", id);
        wmMaterialService.cancelCollect(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 自媒体端素材取消收藏
     *
     * @param id 素材ID
     * @return ResponseResult
     */
    @GetMapping("/del_picture/{id}")
    public ResponseResult<?> deletePicture(@PathVariable @NotNull Integer id) {
        log.info("自媒体端删除素材，{}", id);
        wmMaterialService.deletePicture(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
