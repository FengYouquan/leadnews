package com.youquan.user.service;

import com.youquan.model.user.dto.ApUserLoginDto;
import com.youquan.model.user.vo.ApUserLoginVo;

import java.util.Map;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 12:16
 */
public interface ApUserService {
    /**
     * App端用户登录
     *
     * @param apUserLoginDto 登录参数
     * @return  Map<String,Object>
     */
    Map<String,Object> login(ApUserLoginDto apUserLoginDto);
}
