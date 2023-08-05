package com.ledger.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.common.utils.PageUtils;
import com.ledger.gulimall.ware.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author ledger
 * @email ledgerhhh@gmail.com
 * @date 2023-06-17 17:43:08
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

