package com.ledger.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.ware.entity.WareSkuEntity;

import java.util.Map;

/**
 * 商品库存
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:43:07
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

