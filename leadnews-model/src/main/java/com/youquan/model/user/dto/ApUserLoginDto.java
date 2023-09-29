package com.youquan.model.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
    @Pattern(regexp = "^1[3-9][0-9]{9}$", message = "手机号格式有误")
    @NotBlank(message = "手机号不能为空")
    private String phone;
    /**
     * 密码
     */
    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
