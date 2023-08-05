package com.ledger.gulimall.ware.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.ware.dao.WareSkuDao;
import com.ledger.gulimall.ware.entity.WareSkuEntity;
import com.ledger.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        //skuId: 1
        //wareId: 1676132151101870082

        LambdaQueryWrapper<WareSkuEntity> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(StrUtil.isNotBlank((String)params.get("wareId")),WareSkuEntity::getWareId, params.get("wareId"));
        wrapper.eq(StrUtil.isNotBlank((String)params.get("skuId")),WareSkuEntity::getSkuId, params.get("skuId"));

        IPage<WareSkuEntity> page = page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}