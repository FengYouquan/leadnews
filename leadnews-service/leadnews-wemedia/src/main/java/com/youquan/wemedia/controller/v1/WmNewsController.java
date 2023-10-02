package com.youquan.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youquan.model.common.dto.PageResponseResult;
import com.youquan.model.common.dto.ResponseResult;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.model.wemedia.dto.WmNewsDto;
import com.youquan.model.wemedia.dto.WmNewsPageReqDto;
import com.youquan.model.wemedia.pojo.WmNews;
import com.youquan.wemedia.service.WmNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 12:24
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {
    private final WmNewsService wmNewsService;

    /**
     * 查询文章列表
     *
     * @param wmNewsPageReqDto 查询条件
     * @return ResponseResult
     */
    @PostMapping("/list")
    public ResponseResult<?> list(@RequestBody @NotNull WmNewsPageReqDto wmNewsPageReqDto) {
        log.info("查询文章列表,{}", wmNewsPageReqDto);
        IPage<WmNews> wmNewsPage = wmNewsService.listByUserId(wmNewsPageReqDto);
        PageResponseResult pageResponseResult = new PageResponseResult(
                (int) wmNewsPage.getCurrent(), (int) wmNewsPage.getSize(), (int) wmNewsPage.getTotal()
        );
        return pageResponseResult.ok(AppHttpCodeEnum.SUCCESS.getCode(), wmNewsPage.getRecords());
    }

    /**
     * 自媒体端提交文章
     *
     * @param wmNewsDto 参数
     * @return ResponseResult
     */
    @PostMapping("/submit")
    public ResponseResult<?> submitNews(@RequestBody WmNewsDto wmNewsDto) {
        log.info("自媒体端提交文章，{}", wmNewsDto);
        wmNewsService.submitNews(wmNewsDto);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 根据ID查找文章
     *
     * @param id 文章ID
     * @return ResponseResult
     */
    @GetMapping("/one/{id}")
    public ResponseResult<?> getById(@PathVariable @NotNull Integer id) {
        log.info("根据ID查找文章，id：{}", id);
        return ResponseResult.okResult(wmNewsService.getById(id));
    }
}
