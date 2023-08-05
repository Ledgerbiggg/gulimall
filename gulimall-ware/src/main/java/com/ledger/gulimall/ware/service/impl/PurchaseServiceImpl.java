package com.ledger.gulimall.ware.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ledger.common.constant.WareConstant;
import com.ledger.common.utils.R;
import com.ledger.gulimall.product.entity.SkuInfoEntity;
import com.ledger.gulimall.ware.entity.PurchaseDetailEntity;
import com.ledger.gulimall.ware.entity.WareSkuEntity;
import com.ledger.gulimall.ware.feign.PmsSkuFeignService;
import com.ledger.gulimall.ware.service.PurchaseDetailService;
import com.ledger.gulimall.ware.service.WareSkuService;
import com.ledger.gulimall.ware.vo.MergeVo;
import com.ledger.gulimall.ware.vo.PurChaseItemDoneVo;
import com.ledger.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.ware.dao.PurchaseDao;
import com.ledger.gulimall.ware.entity.PurchaseEntity;
import com.ledger.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Resource
    private PurchaseDetailService purchaseDetailService;
    @Resource
    private WareSkuService wareSkuService;
    @Resource
    private PmsSkuFeignService pmsSkuFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {

        LambdaQueryWrapper<PurchaseEntity> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.CREATE.getCode())
                .or()
                .eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.ASSIGNED.getCode());

        IPage<PurchaseEntity> page = page(
                new Query<PurchaseEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        //合并订单
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null) {
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(0);
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        ArrayList<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> collect = items.stream().map(i -> {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            detailEntity.setId(i);
            detailEntity.setPurchaseId(finalPurchaseId);
            detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return detailEntity;
        }).collect(Collectors.toList());
        purchaseDetailService.saveOrUpdateBatch(collect);
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(finalPurchaseId);
        purchaseEntity.setUpdateTime(new Date());
        updateById(purchaseEntity);
    }

    @Transactional
    @Override
    public void received(List<Long> ids) {
        List<PurchaseEntity> purchaseEntities = listByIds(ids);
        List<PurchaseEntity> collect = purchaseEntities.stream().filter(purchaseEntity ->
                purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.CREATE.getCode() ||
                        purchaseEntity.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .peek(purchaseEntity -> purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode()))
                .collect(Collectors.toList());
        //改变采购单状态
        updateBatchById(collect);
        //改变采购需求状态
        List<PurchaseDetailEntity> detail = purchaseDetailService.listDetailByPurchaseId(ids);
        List<PurchaseDetailEntity> purchaseDetailEntityList = detail.stream().peek(purchaseDetailEntity ->
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode()))
                .collect(Collectors.toList());
        //保存所有的采购项
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);
    }

    @Override
    @Transactional
    public void done(PurchaseDoneVo purchaseDoneVo) {
        //改变采购单状态
        Long id = purchaseDoneVo.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setUpdateTime(new Date());
        //改变采购项状态
        List<PurChaseItemDoneVo> items = purchaseDoneVo.getItems();
        ArrayList<PurChaseItemDoneVo> purChaseItemDoneVosFor3 = new ArrayList<>();
        ArrayList<PurChaseItemDoneVo> purChaseItemDoneVosFor4 = new ArrayList<>();
        for (PurChaseItemDoneVo item : items) {
            Integer status = item.getStatus();
            if (status == WareConstant.PurchaseDetailStatusEnum.FINISH.getCode()) {
                purChaseItemDoneVosFor3.add(item);
            } else if (status == WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode()) {
                purChaseItemDoneVosFor4.add(item);
            }
        }
        if (purChaseItemDoneVosFor4.size() > 0) {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HAS_ERROR.getCode());
        } else {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
        }
        //修改采购项的状态
        List<PurchaseDetailEntity> successPurChase = purChaseItemDoneVosFor3.stream().map(purChaseItemDoneVo -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(purChaseItemDoneVo.getItemId());
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        List<PurchaseDetailEntity> failPurChase = purChaseItemDoneVosFor4.stream().map(purChaseItemDoneVo -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(purChaseItemDoneVo.getItemId());
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.HAS_ERROR.getCode());
            purchaseDetailEntity.setReason(purChaseItemDoneVo.getReason());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        //将所有的失败或者成功的都保存
        purchaseDetailService.updateBatchById(successPurChase);
        purchaseDetailService.updateBatchById(failPurChase);
        //修改采购单状态
        updateById(purchaseEntity);
        //将成功的采购进行入库

        List<Long> ids = successPurChase.stream().map(PurchaseDetailEntity::getId).collect(Collectors.toList());
        if(ids.size()==0) return;
        List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailService.listByIds(ids);
        List<Long> skuIds = purchaseDetailEntityList
                .stream()
                .map(PurchaseDetailEntity::getSkuId)
                .collect(Collectors.toList());
        R skuInfo = pmsSkuFeignService.getSkuInfo(skuIds);

        List<Map<String,Object>> res = (List<Map<String,Object>>)skuInfo.get("data");

        List<SkuInfoEntity> skuList = res.stream().map(hashMap ->
                BeanUtil.mapToBean(hashMap, SkuInfoEntity.class, true, null)).collect(Collectors.toList());

        log.error(skuList.toString());

//        List<SkuInfoEntity> skuList = JSONUtil.toList(data, SkuInfoEntity.class);
        List<WareSkuEntity> wareSkuEntityList = purchaseDetailEntityList.stream().map(purchaseDetailEntity -> {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(purchaseDetailEntity.getSkuId());
            wareSkuEntity.setStock(purchaseDetailEntity.getSkuNum());
            wareSkuEntity.setWareId(purchaseDetailEntity.getWareId());
            for (SkuInfoEntity skuInfoEntity : skuList) {
                boolean equals = skuInfoEntity.getSkuId().equals(purchaseDetailEntity.getSkuId());
                if (equals) {
                    wareSkuEntity.setSkuName(skuInfoEntity.getSkuName());
                }
            }
            return wareSkuEntity;
        }).collect(Collectors.toList());
        //查询所有的库存已经有了的skuId
        List<WareSkuEntity> list = wareSkuService.list();
        List<Long> skuIdList = list.stream().map(WareSkuEntity::getSkuId).collect(Collectors.toList());
        for (WareSkuEntity wareSkuEntity : wareSkuEntityList) {
            Long skuId = wareSkuEntity.getSkuId();
            boolean contains = skuIdList.contains(skuId);
            log.error(contains+"======================");
            if (contains) {
                LambdaUpdateWrapper<WareSkuEntity> wrapper = new LambdaUpdateWrapper<>();
//                update ,,, set ss,ss  where skuid=sss
                wrapper.setSql("stock=stock+" + wareSkuEntity.getStock());
                wrapper.eq(WareSkuEntity::getSkuId, skuId);
                wareSkuService.update(wrapper);
            } else {
                wareSkuService.save(wareSkuEntity);
            }
        }
    }

}