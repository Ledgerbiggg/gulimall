package com.ledger.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.ware.entity.PurchaseEntity;
import com.ledger.gulimall.ware.vo.MergeVo;
import com.ledger.gulimall.ware.vo.PurchaseDoneVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:43:07
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void received(List<Long> ids);

    void done(PurchaseDoneVo purchaseDoneVo);
}

