package com.ledger.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.product.entity.SkuInfoEntity;

import java.util.Map;

/**
 * sku信息
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 11:27:44
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Long saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

