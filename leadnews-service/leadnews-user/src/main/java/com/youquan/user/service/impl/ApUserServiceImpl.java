package com.youquan.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youquan.common.exception.CustomException;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.model.user.dto.ApUserLoginDto;
import com.youquan.model.user.pojo.ApUser;
import com.youquan.model.user.vo.ApUserLoginVo;
import com.youquan.user.mapper.ApUserMapper;
import com.youquan.user.service.ApUserService;
import com.youquan.utils.common.AppJwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/9/29 12:19
 */
@RequiredArgsConstructor
@Service
public class ApUserServiceImpl implements ApUserService {
    private final ApUserMapper apUserMapper;

    /**
     * App端用户登录
     *
     * @param apUserLoginDto 登录参数
     * @return Map<String, Object>
     */
    @Override
    public Map<String, Object> login(ApUserLoginDto apUserLoginDto) {
        HashMap<String, Object> result = new HashMap<>();

        // 游客登录
        if (Objects.equals(apUserLoginDto.getPhone(), "")) {
            result.put("token", AppJwtUtil.getToken(0L));
            return result;
        }

        // 参数校验
        if (!apUserLoginDto.getPhone().trim().matches("^1[3-9]\\d{9}$")) {
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询用户
        ApUser apUser = apUserMapper.selectOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, apUserLoginDto.getPhone()));
        if (apUser == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 比对密码
        String passwordMd5 = DigestUtils.md5DigestAsHex(apUserLoginDto.getPassword().concat(apUser.getSalt()).getBytes());
        if (!Objects.equals(apUser.getPassword(), passwordMd5)) {
            throw new CustomException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 生成JWT并封装结果
        result.put("token", AppJwtUtil.getToken(apUser.getId().longValue()));
        ApUserLoginVo apUserLoginVo = new ApUserLoginVo();
        BeanUtils.copyProperties(apUser, apUserLoginVo);
        result.put("user", apUserLoginVo);
        return result;
    }
}
