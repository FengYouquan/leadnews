package com.youquan.model.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 12:11
 */
@ApiModel(description = "APP端用户登录提交的数据模型")
@Data
public class ApUserLoginDto {
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空")
    private String phone;
    /**
     * 密码
     */
    @ApiModelProperty("密码")
    @NotNull(message = "密码不能为空")
    private String password;
}
