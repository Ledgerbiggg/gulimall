package com.ledger.gulimall.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ledger.gulimall.product.entity.BrandEntity;
import com.ledger.gulimall.product.entity.CategoryEntity;
import com.ledger.gulimall.product.service.BrandService;

import com.ledger.gulimall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.List;
import java.util.function.Consumer;


@SpringBootTest
@Slf4j
class GulimallProductApplicationTests {

    @Resource
    private BrandService brandService;
    @Resource
    private CategoryService categoryService;



    @Test
    void contextLoads() {
        CategoryEntity byId = categoryService.getById("1");
        log.error(byId.toString());

//        LambdaQueryWrapper<BrandEntity> brandEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
//
//
//        brandEntityLambdaQueryWrapper.eq(BrandEntity::getBrandId, 1L);
//
//        List<BrandEntity> list = brandService.list(brandEntityLambdaQueryWrapper);
//        list.forEach(System.out::println);


    }

}
