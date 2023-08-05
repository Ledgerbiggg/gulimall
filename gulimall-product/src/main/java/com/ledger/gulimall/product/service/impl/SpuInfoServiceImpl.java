package com.ledger.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.ledger.common.to.SkuReductionTo;
import com.ledger.common.to.SpuBoundTo;
import com.ledger.common.utils.R;
import com.ledger.gulimall.product.entity.*;
import com.ledger.gulimall.product.feign.CouponFeignService;
import com.ledger.gulimall.product.service.*;
import com.ledger.gulimall.product.vo.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
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

import com.ledger.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    private SpuInfoDescService spuInfoDescService;

    @Resource
    private SpuImagesService spuImagesService;

    @Resource
    private AttrService attrService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuImagesService skuImagesService;

    @Resource
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(spuSaveVo vo) {
        // 操作表格是这样子的pms_spu_
        //  1.保存spu的基本信息 info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtil.copyProperties(vo, spuInfoEntity);
        //保存存入时间
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        //保存spu的id
        Long spuId = saveBaseSpuInfo(spuInfoEntity);
        //  2.保存spu的描述图片 info_desc
        List<String> descript = vo.getDecript();
        //保存描述信息
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        //设置spu的id
        spuInfoDescEntity.setSpuId(spuId);
        String join = String.join(",", descript);
        spuInfoDescEntity.setDecript(join);
        saveSpuInfoDesc(spuInfoDescEntity);
        // 3.保存spu的图片集 images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(images, spuId);
        // todo 4.保存spu的规格参数 attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            //页面没有提交这个，所以要自己查一遍再展示
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuId);
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);
        //  5.保存spu对应的所有sku信息
        // 操作表格是这样子的pms_sku_
        //  5.1 sku的基本信息 info
        List<Skus> skus = vo.getSkus();
        if (skus != null || skus.size() > 0) {
            skus.forEach(sku -> {
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtil.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuId);
                // 5.3 sku的图片信息 images
                List<Images> imagesList = sku
                        .getImages()
                        .stream()
                        .filter(item -> item.getDefaultImg() == 1)
                        .collect(Collectors.toList());
                skuInfoEntity.setSkuDefaultImg(imagesList.size() > 0 ? imagesList.get(0).getImgUrl() : "");
                Long skuId = skuInfoService.saveSkuInfo(skuInfoEntity);
                List<SkuImagesEntity> skuImagesEntityList = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(skuImagesEntity -> StrUtil.isNotBlank(skuImagesEntity.getImgUrl())).collect(Collectors.toList());
                skuImagesService.saveBatch(skuImagesEntityList);
                // 5.3 sku的销售属信息 sale_attr_value
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attr.stream().map(attr1 -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = BeanUtil.copyProperties(attr1, SkuSaleAttrValueEntity.class);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
                // todo 6 sku的积分信息 spu_bounds
                Bounds bounds = vo.getBounds();
                SpuBoundTo spuBoundTo = new SpuBoundTo();
                BeanUtil.copyProperties(bounds, spuBoundTo);
                spuBoundTo.setSpuId(spuId);
                R r1 = couponFeignService.saveSpuBounds(spuBoundTo);
                if (r1.getCode() != 0) {
                    log.error("远程保存sku积分信息失败");
                }
                // todo 5.4 sku的优惠信息、满减优惠信息 gulimall_sms sku_ladder/ sku_full_reduction / sku_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtil.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                //优惠和满减要判断是否存在数值大于0
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
                    R r = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (r.getCode() != 0) {
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //关键字不为空就查询关键字
        wrapper.and(StrUtil.isNotBlank((String) params.get("key")),
                i -> i.like("spu_name", params.get("key")).or().eq("id", params.get("key")));
        wrapper.eq(StrUtil.isNotBlank((String) params.get("status")), "publish_status", params.get("status"));
        wrapper.eq(StrUtil.isNotBlank((String) params.get("brandId")), "brand_id", params.get("brandId"));
        wrapper.eq(StrUtil.isNotBlank((String) params.get("catelogId")), "catalog_id", params.get("catelogId"));

        IPage<SpuInfoEntity> page = page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 将描述保存
     *
     * @param spuInfoDescEntity
     */
    private void saveSpuInfoDesc(SpuInfoDescEntity spuInfoDescEntity) {
        spuInfoDescService.save(spuInfoDescEntity);
    }

    /**
     * 将spu的基本信息保存
     *
     * @param spuInfoEntity
     * @return
     */
    private Long saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
        save(spuInfoEntity);
        return spuInfoEntity.getId();
    }

}