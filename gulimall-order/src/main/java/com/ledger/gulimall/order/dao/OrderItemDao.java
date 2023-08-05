package com.ledger.gulimall.order.dao;

import com.ledger.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:31:11
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
