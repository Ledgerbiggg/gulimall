package com.ledger.gulimall.member.dao;

import com.ledger.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:39:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
