package com.ledger.gulimall.coupon.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ledger.common.to.MemberPrice;
import com.ledger.common.to.SkuReductionTo;
import com.ledger.gulimall.coupon.entity.MemberPriceEntity;
import com.ledger.gulimall.coupon.entity.SkuLadderEntity;
import com.ledger.gulimall.coupon.service.MemberPriceService;
import com.ledger.gulimall.coupon.service.SkuLadderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.coupon.dao.SkuFullReductionDao;
import com.ledger.gulimall.coupon.entity.SkuFullReductionEntity;
import com.ledger.gulimall.coupon.service.SkuFullReductionService;

import javax.annotation.Resource;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Resource
    private SkuLadderService skuLadderService;

    @Resource
    private MemberPriceService memberPriceService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //1. 保存满减打折
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuLadderEntity.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        if (skuReductionTo.getFullCount() > 0) {
            skuLadderService.save(skuLadderEntity);
        }
        //2. 保存满减信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtil.copyProperties(skuReductionTo, skuFullReductionEntity);
        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) > 0) {
            save(skuFullReductionEntity);
        }
        //3. 会员价格
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice
                .stream()
                .filter(memberPrice12 -> memberPrice12.getPrice().compareTo(new BigDecimal("0")) > 0)
                .map(memberPrice1 -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                    memberPriceEntity.setMemberLevelId(memberPrice1.getId());
                    memberPriceEntity.setMemberLevelName(memberPrice1.getName());
                    memberPriceEntity.setMemberPrice(memberPrice1.getPrice());
                    //叠加其他优惠
                    memberPriceEntity.setAddOther(1);
                    return memberPriceEntity;
                }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);

    }

}