package com.ledger.gulimall.product.dao;

import com.ledger.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 11:27:44
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
