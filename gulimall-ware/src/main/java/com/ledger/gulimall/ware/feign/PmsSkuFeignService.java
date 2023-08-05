package com.ledger.gulimall.ware.feign;

import com.ledger.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@FeignClient("gulimall-product")
public interface PmsSkuFeignService {
    @PostMapping("/product/skuinfo/listByIds")
    R getSkuInfo(@RequestBody List<Long> ids);
}
