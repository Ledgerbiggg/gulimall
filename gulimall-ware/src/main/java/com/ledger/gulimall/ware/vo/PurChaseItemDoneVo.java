package com.ledger.gulimall.ware.vo;

import lombok.Data;

/**
 * @author ledger
 * @version 1.0
 **/
@Data
public class PurChaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
