package com.youquan.model.wemedia.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 23:32
 */
@Data
public class WmLoginDto {
    /**
     * 用户名
     */
    @NotBlank
    private String name;
    /**
     * 密码
     */
    @NotBlank
    private String password;
}
