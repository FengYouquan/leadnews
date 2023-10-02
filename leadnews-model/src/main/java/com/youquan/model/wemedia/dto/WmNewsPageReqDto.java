package com.youquan.model.wemedia.dto;

import com.youquan.model.common.dto.PageRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 12:23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WmNewsPageReqDto extends PageRequestDto {
    /**
     * 状态
     */
    private Short status;
    /**
     * 开始时间
     */
    private Date beginPubDate;
    /**
     * 结束时间
     */
    private Date endPubDate;
    /**
     * 所属频道ID
     */
    private Integer channelId;
    /**
     * 关键字
     */
    private String keyword;
}
