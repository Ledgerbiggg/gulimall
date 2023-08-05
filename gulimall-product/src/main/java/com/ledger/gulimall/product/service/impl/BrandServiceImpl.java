package com.ledger.gulimall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ledger.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.product.dao.BrandDao;
import com.ledger.gulimall.product.entity.BrandEntity;
import com.ledger.gulimall.product.service.BrandService;

import javax.annotation.Resource;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {


    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //模糊查询
        String key = (String)params.get("key");
        LambdaQueryWrapper<BrandEntity> brandEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();


        if(StrUtil.isNotBlank(key)){
            brandEntityLambdaQueryWrapper.eq(BrandEntity::getBrandId,key).or().like(BrandEntity::getName,key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                brandEntityLambdaQueryWrapper
        );


        return new PageUtils(page);
    }

    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段一直
        updateById(brand);
        if(!brand.getName().isEmpty()){
            categoryBrandRelationService.updateBrand(brand.getBrandId(),brand.getName());
        }


    }

}