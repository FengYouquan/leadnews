package com.youquan.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youquan.model.wemedia.dto.WmLoginDto;
import com.youquan.model.wemedia.pojo.WmUser;

import java.util.Map;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 23:29
 */
public interface WmUserService extends IService<WmUser> {
    /**
     * 自媒体端用户登录
     *
     * @param wmLoginDto 登录参数
     * @return Map <String, Object>
     */
    Map<String, Object> login(WmLoginDto wmLoginDto);
}
