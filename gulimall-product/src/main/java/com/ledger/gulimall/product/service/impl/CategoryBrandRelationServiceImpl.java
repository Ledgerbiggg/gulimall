package com.ledger.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ledger.gulimall.product.entity.BrandEntity;
import com.ledger.gulimall.product.entity.CategoryEntity;
import com.ledger.gulimall.product.service.AttrService;
import com.ledger.gulimall.product.service.BrandService;
import com.ledger.gulimall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.product.dao.CategoryBrandRelationDao;
import com.ledger.gulimall.product.entity.CategoryBrandRelationEntity;
import com.ledger.gulimall.product.service.CategoryBrandRelationService;

import javax.annotation.Resource;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Resource
    CategoryService categoryService;
    @Resource
    BrandService brandService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryBrandRelationEntity> getCatelogList(Long brandId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> categoryBrandRelationEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();

        categoryBrandRelationEntityLambdaQueryWrapper.eq(CategoryBrandRelationEntity::getBrandId, brandId);
        return list(categoryBrandRelationEntityLambdaQueryWrapper);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        BrandEntity brandEntity = brandService.getById(brandId);
        String categoryEntityName = categoryEntity.getName();
        String brandEntityName = brandEntity.getName();
        categoryBrandRelation.setBrandName(brandEntityName);
        categoryBrandRelation.setCatelogName(categoryEntityName);
        save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        LambdaUpdateWrapper<CategoryBrandRelationEntity> categoryBrandRelationEntityLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        categoryBrandRelationEntityLambdaUpdateWrapper.eq(CategoryBrandRelationEntity::getBrandId,brandId);

        update(categoryBrandRelationEntity,categoryBrandRelationEntityLambdaUpdateWrapper);

    }

    @Override
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId,name);
    }

    /**
     * 查询分类的品牌
     * @param catId
     * @return
     */
    @Override
    public  List<BrandEntity> getBrandsListByCatId(Long catId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryBrandRelationEntity::getCatelogId,catId);
        List<CategoryBrandRelationEntity> list = list(wrapper);

        List<Long> brandIds = list.stream().map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());

        return brandService.listByIds(brandIds);
    }

}