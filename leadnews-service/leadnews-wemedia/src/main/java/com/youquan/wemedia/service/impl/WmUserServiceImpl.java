package com.youquan.wemedia.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youquan.common.exception.CustomException;
import com.youquan.model.common.enums.AppHttpCodeEnum;
import com.youquan.model.wemedia.dto.WmLoginDto;
import com.youquan.model.wemedia.pojo.WmUser;
import com.youquan.model.wemedia.vo.WmLoginVo;
import com.youquan.utils.common.AppJwtUtil;
import com.youquan.wemedia.mapper.WmUserMapper;
import com.youquan.wemedia.service.WmUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/1 23:33
 */
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {
    /**
     * 自媒体端用户登录
     *
     * @param wmLoginDto 登录参数
     * @return Map <String, Object>
     */
    @Override
    public Map<String, Object> login(WmLoginDto wmLoginDto) {
        // 查询用户
        WmUser wmUser = this.getOne(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, wmLoginDto.getName()));
        if (wmUser == null) {
            throw new CustomException(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 比对密码
        String passwordMd5 = DigestUtils.md5DigestAsHex(wmLoginDto.getPassword().concat(wmUser.getSalt()).getBytes());
        if (!Objects.equals(wmUser.getPassword(), passwordMd5)) {
            throw new CustomException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        // 生成JWT并封装结果
        HashMap<String, Object> result = new HashMap<>();
        result.put("token", AppJwtUtil.getToken(wmUser.getId().longValue()));
        WmLoginVo wmLoginVo = new WmLoginVo();
        BeanUtils.copyProperties(wmUser, wmLoginVo);
        result.put("user", wmLoginVo);
        return result;
    }
}
