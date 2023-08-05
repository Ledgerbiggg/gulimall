package com.ledger.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.ledger.gulimall.product.entity.AttrGroupEntity;
import com.ledger.gulimall.product.entity.dto.AttrGroupEntityDTO;
import com.ledger.gulimall.product.vo.AttrGroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 11:27:44
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    AttrGroupEntityDTO getInfo(Long attrGroupId);

    List<Long> findParentPath(Long categoryId, List<Long> path);

    void deleteRelation(List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntitys);

    List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId);
}

