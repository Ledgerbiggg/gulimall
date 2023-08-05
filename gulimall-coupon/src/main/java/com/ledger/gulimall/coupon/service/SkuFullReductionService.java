package com.ledger.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.to.SkuReductionTo;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:27:28
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

