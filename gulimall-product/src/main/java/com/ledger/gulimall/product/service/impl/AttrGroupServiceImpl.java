package com.ledger.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ledger.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.ledger.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.ledger.gulimall.product.entity.AttrEntity;
import com.ledger.gulimall.product.entity.CategoryEntity;
import com.ledger.gulimall.product.entity.dto.AttrGroupEntityDTO;
import com.ledger.gulimall.product.service.AttrAttrgroupRelationService;
import com.ledger.gulimall.product.service.AttrService;
import com.ledger.gulimall.product.service.CategoryService;
import com.ledger.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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

import com.ledger.gulimall.product.dao.AttrGroupDao;
import com.ledger.gulimall.product.entity.AttrGroupEntity;
import com.ledger.gulimall.product.service.AttrGroupService;

import javax.annotation.Resource;
import javax.management.relation.RelationService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    CategoryService categoryService;
    @Resource
    AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Resource
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Resource
    AttrService attrService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        LambdaQueryWrapper<AttrGroupEntity> attrGroupEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        attrGroupEntityLambdaQueryWrapper.and(StrUtil.isNotBlank(key), obj -> {
            obj.like(AttrGroupEntity::getAttrGroupName, key).or().like(AttrGroupEntity::getAttrGroupId, key);
        });
        if (catelogId == 0) {

            IPage<AttrGroupEntity> page =
                    this.page(new Query<AttrGroupEntity>().getPage(params), attrGroupEntityLambdaQueryWrapper);

            return new PageUtils(page);

        } else {
            attrGroupEntityLambdaQueryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), attrGroupEntityLambdaQueryWrapper);

            return new PageUtils(page);

        }
    }

    @Override
    public AttrGroupEntityDTO getInfo(Long attrGroupId) {
        //返回的类型
        AttrGroupEntityDTO attrGroupEntityDTO = new AttrGroupEntityDTO();
        //查询出已有的数据
        AttrGroupEntity attrGroupEntity = getById(attrGroupId);
        //新增父类的路径
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(attrGroupEntity.getCatelogId(), paths);
        Collections.reverse(parentPath);
        BeanUtil.copyProperties(attrGroupEntity, attrGroupEntityDTO);
        attrGroupEntityDTO.setCatelogPath(parentPath);
        return attrGroupEntityDTO;
    }

    public List<Long> findParentPath(Long categoryId, List<Long> path) {

        CategoryEntity categoryEntity = categoryService.getById(categoryId);
        if (categoryEntity != null) {
            path.add(categoryEntity.getCatId());
            Long parentCid = categoryEntity.getParentCid();
            path = findParentPath(parentCid, path);
        }
        return path;

    }

    @Override
    public void deleteRelation(List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntitys) {
        attrAttrgroupRelationDao.deleteBatchRelation(attrAttrgroupRelationEntitys);
    }

    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {

        LambdaQueryWrapper<AttrGroupEntity> attrGroupEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        attrGroupEntityLambdaQueryWrapper.eq(AttrGroupEntity::getCatelogId, catelogId);

        //根据id查出所有的分组
        List<AttrGroupEntity> attrGroupEntityList = list(attrGroupEntityLambdaQueryWrapper);
        List<Long> attrGroupIds = attrGroupEntityList
                .stream()
                .map(AttrGroupEntity::getAttrGroupId)
                .collect(Collectors.toList());

        //查询出所有分组对应的属性关联
        LambdaQueryWrapper<AttrAttrgroupRelationEntity> attrEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        attrEntityLambdaQueryWrapper.in(AttrAttrgroupRelationEntity::getAttrGroupId, attrGroupIds);
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrAttrgroupRelationService.list(attrEntityLambdaQueryWrapper);

        //根据属性关联查询出属性绑定到vo
        return attrGroupEntityList.stream().map(attrGroupEntity -> {
            AttrGroupWithAttrsVo attrGroupWithAttrsVo = BeanUtil.copyProperties(attrGroupEntity, AttrGroupWithAttrsVo.class);
            attrGroupWithAttrsVo.setAttrs(getAttrList(attrGroupEntity.getAttrGroupId(), attrgroupRelationEntityList));
            return attrGroupWithAttrsVo;
        }).collect(Collectors.toList());
    }

    private List<AttrEntity> getAttrList(Long attrGroupId, List<AttrAttrgroupRelationEntity> attrEntityList) {
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrEntityList
                .stream()
                .filter(attrAttrgroupRelationEntity -> attrAttrgroupRelationEntity.getAttrGroupId().equals(attrGroupId))
                .collect(Collectors.toList());

        List<Long> attrIds = attrgroupRelationEntityList
                .stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        return attrIds.size()==0?new ArrayList<>():attrService.listByIds(attrIds);
    }


}