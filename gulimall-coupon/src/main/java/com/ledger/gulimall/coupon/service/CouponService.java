package com.ledger.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:27:29
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

