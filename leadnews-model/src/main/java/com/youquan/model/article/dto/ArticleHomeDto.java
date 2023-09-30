package com.youquan.model.article.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/30 21:54
 */
@Data
public class ArticleHomeDto {
    /**
     * 最大时间
     */
    @NotEmpty(message = "最大时间不能为空")
    private Date maxBehotTime;
    /**
     * 最小时间
     */
    @NotEmpty(message = "最小时间不能为空")
    private Date minBehotTime;
    /**
     * 分页size
     */
    @Min(value = 1L, message = "每页记录数最小为1条")
    @Max(value = 50L, message = "每页记录数最大为50条")
    private Integer size;
    /**
     * 频道ID
     */
    @NotBlank(message = "频道ID不能为空")
    private String tag;
}
