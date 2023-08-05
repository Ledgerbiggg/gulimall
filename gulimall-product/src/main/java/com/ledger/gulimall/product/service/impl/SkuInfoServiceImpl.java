package com.ledger.gulimall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Struct;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.product.dao.SkuInfoDao;
import com.ledger.gulimall.product.entity.SkuInfoEntity;
import com.ledger.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public Long saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        save(skuInfoEntity);
        return skuInfoEntity.getSkuId();
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> wrapper = new LambdaQueryWrapper<>();
        //t: 1688451770388
        //page: 1
        //limit: 10
        //key: 1
        //catelogId: 225
        //brandId: 1673303380476919809
        //min: 10
        //max: 20
        wrapper.and(StrUtil.isNotBlank((String) params.get("key")), i -> {
            i.eq(SkuInfoEntity::getSkuId, params.get("key")).or().like(SkuInfoEntity::getSkuName, params.get("key"));
        });
        wrapper.eq(StrUtil.isNotBlank((String) params.get("catelogId")) && !params.get("catelogId").equals("0")
                , SkuInfoEntity::getCatalogId
                , params.get("catelogId"));
        wrapper.eq(StrUtil.isNotBlank((String) params.get("brandId")) && !params.get("brandId").equals("0"),
                SkuInfoEntity::getBrandId,
                params.get("brandId"));
        if(StrUtil.isNotBlank((String) params.get("min"))){
            wrapper.gt(
                    SkuInfoEntity::getPrice,
                    new BigDecimal((String) params.get("min")));
        }

        if(StrUtil.isNotBlank((String) params.get("max"))){
            wrapper.lt(
                    SkuInfoEntity::getPrice,
                    new BigDecimal((String) params.get("max")));
        }


        IPage<SkuInfoEntity> page = page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

}