package com.ledger.gulimall.product.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ledger.gulimall.product.entity.AttrGroupEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class AttrGroupEntityDTO extends AttrGroupEntity {

    private List<Long> catelogPath;
}
