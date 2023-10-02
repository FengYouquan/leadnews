package com.youquan.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youquan.model.wemedia.pojo.WmChannel;
import com.youquan.wemedia.mapper.WmChannelMapper;
import com.youquan.wemedia.service.WmChannelService;
import org.springframework.stereotype.Service;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 12:18
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
}
