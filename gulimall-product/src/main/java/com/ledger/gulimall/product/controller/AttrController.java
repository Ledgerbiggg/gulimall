package com.ledger.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.ledger.gulimall.product.entity.ProductAttrValueEntity;
import com.ledger.gulimall.product.service.ProductAttrValueService;
import com.ledger.gulimall.product.vo.AttrRespVo;
import com.ledger.gulimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ledger.gulimall.product.entity.AttrEntity;
import com.ledger.gulimall.product.service.AttrService;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.R;

import javax.annotation.Resource;
import javax.xml.crypto.Data;


/**
 * 商品属性
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-26 14:00:26
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Resource
    private ProductAttrValueService productAttrValueService;


    //http://localhost/api/product/attr/update/1676073652808376321
    @RequestMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R updateSpuAttr(@RequestBody List<ProductAttrValueEntity> entities, @PathVariable Long spuId){
        productAttrValueService.updateSpuAttr(spuId,entities);

        return R.ok();
    }

    //http://localhost/api/product/attr/base/listforspu/1676073652808376321
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities= productAttrValueService.baseAttrListForSpu(spuId);

        return R.ok().put("data",entities);
    }



    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@PathVariable Long catelogId,
                          @RequestParam Map<String, Object> params,
                          @PathVariable("attrType") String type){

        PageUtils pages= attrService.queryBaseAttrPage(params,catelogId,type);

        return R.ok().put("page",pages);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("product:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrRespVo attr= attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVo attr){
        attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateAttr(attr);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
