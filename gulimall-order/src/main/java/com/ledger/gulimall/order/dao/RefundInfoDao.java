package com.ledger.gulimall.order.dao;

import com.ledger.gulimall.order.entity.RefundInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 退款信息
 * 
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:31:11
 */
@Mapper
public interface RefundInfoDao extends BaseMapper<RefundInfoEntity> {
	
}
