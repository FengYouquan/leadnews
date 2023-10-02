package com.youquan.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youquan.model.wemedia.pojo.WmNewsMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Fengyouquan
 * @version 1.0
 * @date 2023/10/2 15:18
 */
@Mapper
public interface WmNewsMaterialMapper extends BaseMapper<WmNewsMaterial> {
    /**
     * 批量保存图文元素和素材的关联关系
     *
     * @param wmMaterialIdList 素材ID集合
     * @param newsId           图文ID
     * @param type             保存类型
     */
    void insertRelationship(@Param("wmMaterialIdList") List<Integer> wmMaterialIdList,
                            @Param("newsId") Integer newsId,
                            @Param("type") Short type);
}
