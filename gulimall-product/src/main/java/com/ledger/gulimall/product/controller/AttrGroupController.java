package com.ledger.gulimall.product.controller;

import java.util.List;
import java.util.Map;


import com.ledger.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.ledger.gulimall.product.entity.AttrEntity;
import com.ledger.gulimall.product.entity.dto.AttrGroupEntityDTO;
import com.ledger.gulimall.product.service.AttrAttrgroupRelationService;
import com.ledger.gulimall.product.service.AttrService;
import com.ledger.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ledger.gulimall.product.entity.AttrGroupEntity;
import com.ledger.gulimall.product.service.AttrGroupService;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.R;

import javax.annotation.Resource;


/**
 * 属性分组
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-26 14:00:26
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Resource
    private AttrService attrService;
    @Resource
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 新增分组的属性
     * @return
     */
    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntitys){
        attrAttrgroupRelationService.saveBatchAttr(attrAttrgroupRelationEntitys);

        return R.ok();
    }

    /**
     * 通过分类id获取属性组别和属性组别中的所有属性
     * @param catelogId
     * @return
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttr(@PathVariable("catelogId") Long catelogId){
        List<AttrGroupWithAttrsVo> attrGroupWithAttrsVos=attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);

        return R.ok().put("data",attrGroupWithAttrsVos);
    }



    /**
     * 获取所有的组别关联的属性信息
     * @param attrgroupId
     * @return
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
       List<AttrEntity> attrEntities=attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",attrEntities);
    }

    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,@RequestParam Map<String,Object> params){

        PageUtils pageUtils=attrService.getNoRelationAttr(attrgroupId,params);

        return R.ok().put("page",pageUtils);
    }



    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Long catelogId){
        PageUtils page = attrGroupService.queryPage(params, catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){

        AttrGroupEntityDTO attrGroupEntityDTO= attrGroupService.getInfo(attrGroupId);
        return R.ok().put("attrGroup", attrGroupEntityDTO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/attr/relation/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R deleteRelation(@RequestBody List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntitys){
		attrGroupService.deleteRelation(attrAttrgroupRelationEntitys);
        return R.ok();
    }

}
