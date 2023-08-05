package com.ledger.gulimall.ware.service.impl;

import com.ledger.gulimall.ware.entity.PurchaseDetailEntity;
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
class PurchaseDetailServiceImplTest {
    @Resource
    private PurchaseDetailServiceImpl purchaseDetailService;

    @Test
    void listDetailByPurchaseId() {
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(1676158511346196481L);
        List<PurchaseDetailEntity> detail =
                purchaseDetailService.listDetailByPurchaseId(longs);
        log.error(detail.toString());
    }
}