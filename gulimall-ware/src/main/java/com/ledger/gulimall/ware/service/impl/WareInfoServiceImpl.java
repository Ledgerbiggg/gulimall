package com.ledger.gulimall.ware.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.common.utils.PageUtils;
import com.ledger.common.utils.Query;

import com.ledger.gulimall.ware.dao.WareInfoDao;
import com.ledger.gulimall.ware.entity.WareInfoEntity;
import com.ledger.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        LambdaQueryWrapper<WareInfoEntity> wrapper = new LambdaQueryWrapper<>();

        String key = (String) params.get("key");
        wrapper.and(
                StrUtil.isNotBlank(key),
                i -> i.eq(WareInfoEntity::getId, key)
                        .or()
                        .like(WareInfoEntity::getName, key)
                        .or()
                        .like(WareInfoEntity::getAddress, key)
                        .or().like(WareInfoEntity::getAreacode, key)
        );
        IPage<WareInfoEntity> page = page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}