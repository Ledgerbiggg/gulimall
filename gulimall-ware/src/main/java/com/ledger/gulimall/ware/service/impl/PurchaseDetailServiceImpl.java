package com.ledger.gulimall.ware.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.ware.dao.PurchaseDetailDao;
import com.ledger.gulimall.ware.entity.PurchaseDetailEntity;
import com.ledger.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        //t: 1688457695800
        //page: 1
        //limit: 10
        //key: 1
        //status: 1
        //wareId: 1676132151101870082
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(StrUtil.isNotBlank((String) params.get("status")),
                PurchaseDetailEntity::getStatus,
                params.get("status"));

        wrapper.and(StrUtil.isNotBlank((String) params.get("key")),
                i -> i.eq(PurchaseDetailEntity::getSkuId, params.get("key"))
                        .or()
                        .like(PurchaseDetailEntity::getSkuNum, params.get("key"))
        );

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);

    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(List<Long> ids) {
        LambdaQueryWrapper<PurchaseDetailEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PurchaseDetailEntity::getPurchaseId, ids);
        return list(wrapper);
    }

}