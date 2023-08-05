package com.ledger.gulimall.product.service.impl;

import com.ledger.gulimall.product.entity.dto.AttrGroupEntityDTO;
import com.ledger.gulimall.product.service.AttrGroupService;
import com.ledger.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author ledger
 * @version 1.0
 **/
@SpringBootTest
@Slf4j
class AttrGroupServiceImplTest {
    @Resource
    private AttrGroupService attrGroupService;

    @Test
    void getInfo() {
        AttrGroupEntityDTO info = attrGroupService.getInfo(1673285944352444417L);
        log.info(info.toString());
    }
}