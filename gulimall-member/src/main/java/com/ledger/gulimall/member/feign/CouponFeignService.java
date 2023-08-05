package com.ledger.gulimall.member.feign;

import com.ledger.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ledger
 * @version 1.0
 **/
@FeignClient("gulimall-coupon")
@RequestMapping("/coupon/coupon")
public interface CouponFeignService {
    @GetMapping("/member-coupon")
    R memberCoupon();
}
