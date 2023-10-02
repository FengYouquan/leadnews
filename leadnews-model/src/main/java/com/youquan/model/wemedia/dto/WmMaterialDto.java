package com.youquan.model.wemedia.dto;

import com.youquan.model.common.dto.PageRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 1:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WmMaterialDto extends PageRequestDto {
    /**
     * 1 收藏
     * 0 未收藏
     */
    private Short isCollection;
}
