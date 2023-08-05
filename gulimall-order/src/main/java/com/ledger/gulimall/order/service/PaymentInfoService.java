package com.ledger.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:31:11
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

