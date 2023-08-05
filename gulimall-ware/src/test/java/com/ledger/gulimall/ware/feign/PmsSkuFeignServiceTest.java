package com.ledger.gulimall.ware.feign;

import com.ledger.common.utils.R;
import com.ledger.gulimall.product.entity.SkuInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ledger
 * @version 1.0
 **/
@SpringBootTest
@Slf4j
class PmsSkuFeignServiceTest {
    @Resource
    private PmsSkuFeignService pmsSkuFeignService;

    @Test
    void getSkuInfo() {
        ArrayList<Long> longs = new ArrayList<>();

        longs.add(1676073653601099778L);
        R skuInfo = pmsSkuFeignService.getSkuInfo(longs);
        List<SkuInfoEntity> data = (List<SkuInfoEntity>)skuInfo.get("data");
        log.error(data.toString());
    }
}