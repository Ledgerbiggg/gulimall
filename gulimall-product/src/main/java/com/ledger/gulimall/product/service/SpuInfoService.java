package com.ledger.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.product.entity.SpuInfoEntity;
import com.ledger.gulimall.product.vo.spuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 11:27:44
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(spuSaveVo spuSaveVo);

    PageUtils queryPageByCondition(Map<String, Object> params);
}

