package com.ledger.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ledger.gulimall.product.entity.dto.CategoryEntityDTO;
import com.ledger.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

import com.ledger.gulimall.product.dao.CategoryDao;
import com.ledger.gulimall.product.entity.CategoryEntity;
import com.ledger.gulimall.product.service.CategoryService;

import javax.annotation.Resource;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * @return 所有分类的树形结构
     */
    @Override
    public List<CategoryEntityDTO> listWithTree() {
        List<CategoryEntityDTO> list = list()
                .stream()
                .map(categoryEntity -> BeanUtil.copyProperties(categoryEntity, CategoryEntityDTO.class))
                .collect(Collectors.toList());
        //2.组成父子结构
        List<CategoryEntityDTO> collect = list
                .stream()
                .filter(categoryEntityDTO -> categoryEntityDTO.getParentCid() == 0)  // 过滤出顶级分类
                .peek(categoryEntityDTO -> {
                    // 将CategoryEntityDTO对象转换为CategoryEntity对象，并设置其子分类
                    categoryEntityDTO.setChildren(getChildren(categoryEntityDTO, list));
                })
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))  // 按照分类的排序字段进行排序
                .collect(Collectors.toList());// 将结果收集到一个List中并返回
        return collect;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        removeByIds(asList);

    }

    @Override
    public void updateCascade(CategoryEntity category) {
        updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

    }


    /**
     * @param categoryEntityDTO  指定类别
     * @param categoryEntityDTOS 所有类别列表
     * @return 指定类别的子类别列表
     */
    private List<CategoryEntityDTO> getChildren(CategoryEntityDTO categoryEntityDTO, List<CategoryEntityDTO> categoryEntityDTOS) {
        return categoryEntityDTOS
                .stream()  // 将类别列表转换为Stream
                .filter(categoryEntityDTO1 -> categoryEntityDTO1.getParentCid().equals(categoryEntityDTO.getCatId()))  // 过滤出具有指定父类别ID的类别
                .peek(categoryEntityDTO12 -> {
                    // 递归获取子类别列表，并将其设置为当前类别的子类别
                    List<CategoryEntityDTO> children = getChildren(categoryEntityDTO12, categoryEntityDTOS);
                    categoryEntityDTO12.setChildren(children);
                })
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))  // 按照类别的排序字段进行排序
                .collect(Collectors.toList());  // 将结果收集到一个List中并返回
    }
}