package com.ledger.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ledger.common.constant.ProductConstant;
import com.ledger.common.utils.Constant;
import com.ledger.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.ledger.gulimall.product.entity.AttrGroupEntity;
import com.ledger.gulimall.product.entity.CategoryEntity;
import com.ledger.gulimall.product.service.AttrAttrgroupRelationService;
import com.ledger.gulimall.product.service.AttrGroupService;
import com.ledger.gulimall.product.service.CategoryService;
import com.ledger.gulimall.product.vo.AttrRespVo;
import com.ledger.gulimall.product.vo.AttrVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.product.dao.AttrDao;
import com.ledger.gulimall.product.entity.AttrEntity;
import com.ledger.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Resource
    private AttrGroupService attrGroupService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtil.copyProperties(attr, attrEntity);
        // 1.保存基本数据
        save(attrEntity);
        //2.保存属性和分组的关联数据
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type) {
        LambdaQueryWrapper<AttrEntity> attrEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断是基本属性还是销售属性
        attrEntityLambdaQueryWrapper.eq(AttrEntity::getAttrType, "base".equalsIgnoreCase(type) ?
                ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        //如果有分类id的话,就添加查询分类id
        if (catelogId != 0) {
            attrEntityLambdaQueryWrapper.eq(AttrEntity::getCatelogId, catelogId);
        }
        //获取关键字搜索
        String key = (String) params.get("key");
        if (StrUtil.isNotBlank(key)) {
            attrEntityLambdaQueryWrapper.and(obj -> {
                obj.like(AttrEntity::getAttrName, key).or().like(AttrEntity::getAttrId, key);
            });
        }
        //查询分页
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                attrEntityLambdaQueryWrapper
        );
        //获取分页记录
        List<AttrEntity> records = page.getRecords();
        //将里面的AttrEntity增加两个字段，分组名称和分类名称
        List<AttrRespVo> respVoList = records.stream().map(attrEntity -> {
            //属性拷贝
            AttrRespVo attrRespVo = BeanUtil.copyProperties(attrEntity, AttrRespVo.class);
            LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrEntity.getAttrId());
            //根据关联表查询分组
            AttrAttrgroupRelationEntity one = attrAttrgroupRelationService.getOne(wrapper);
            if (one != null) {
                //查出组的信息
                Long attrGroupId = one.getAttrGroupId();
                if (attrGroupId != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(one.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            //根据里面的分类id查询分类
            CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            return attrRespVo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(respVoList);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = getById(attrId);
        AttrRespVo attrRespVo = BeanUtil.copyProperties(attrEntity, AttrRespVo.class);
        //分组id
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attrId);
        //设置分组信息
        AttrAttrgroupRelationEntity one = attrAttrgroupRelationService.getOne(wrapper);
        if (one != null) {
            Long attrGroupId = one.getAttrGroupId();
            if (attrGroupId != null) {
                attrRespVo.setAttrGroupId(attrGroupId);
            }
        }
        //设置分类路径
        List<Long> parentPath = attrGroupService.findParentPath(attrRespVo.getCatelogId(), new ArrayList<Long>());
        Collections.reverse(parentPath);
        attrRespVo.setCatelogPath(parentPath);
        //设置分类的名称
        CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attr) {
        AttrEntity attrEntity = BeanUtil.copyProperties(attr, AttrEntity.class);
        //将基本信息保存
        updateById(attrEntity);
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrAttrgroupRelationEntity::getAttrId, attr.getAttrId());
        //修改分组关联
        if (attr.getAttrType().equals(ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode())) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(wrapper);
            if (attrAttrgroupRelationEntity == null) {
                attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            }
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationService.saveOrUpdate(attrAttrgroupRelationEntity);
        }
    }

    /**
     * 根据分组id查找关联的所有基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttrAttrgroupRelationEntity::getAttrGroupId, attrgroupId);
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationService.list(wrapper);
        List<Long> attrList = list
                .stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        return attrList.size() == 0 ? null : listByIds(attrList);
    }

    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //1. 当前分组只能关联自己所属的分类里面的属性
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrgroupId);
        //1.1 将自己所在类的所有分组查出来
        LambdaQueryWrapper<AttrGroupEntity> attrGroupEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long catelogId = attrGroupEntity.getCatelogId();
        attrGroupEntityLambdaQueryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        //获取所有除了自己以外的并且同一个分类下的属性分组
        List<AttrGroupEntity> list = attrGroupService.list(attrGroupEntityLambdaQueryWrapper);
        List<Long> groupIds = list.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        //2. 当前分组只能关联别的分组没有引用的属性
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> wrapper = new LambdaQueryWrapper<>();
        //获取分组的id
        wrapper.in(AttrAttrgroupRelationEntity::getAttrGroupId, groupIds);
        //获取所有属性
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationService.list(wrapper);
        //获取所有的属性的id
        List<Long> attrIds = attrAttrgroupRelationEntities
                .stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        //获取所有的属性的id
        LambdaQueryWrapper<AttrEntity> attrEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        attrEntityLambdaQueryWrapper.eq(AttrEntity::getCatelogId,catelogId);
        attrEntityLambdaQueryWrapper.eq(AttrEntity::getAttrType, ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());

        attrEntityLambdaQueryWrapper.notIn(attrIds.size()>0,AttrEntity::getAttrId,attrIds);
        attrEntityLambdaQueryWrapper.like(StrUtil.isNotBlank((String)params.get("key")),AttrEntity::getAttrId,params.get("key"));
        attrEntityLambdaQueryWrapper.like(StrUtil.isNotBlank((String)params.get("key")),AttrEntity::getAttrName,params.get("key"));
        List<AttrEntity> attrEntities = attrService.list(attrEntityLambdaQueryWrapper);
        IPage<AttrEntity> page = page(new Query<AttrEntity>().getPage(params), new LambdaQueryWrapper<>());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(attrEntities);

        return pageUtils;
    }
}