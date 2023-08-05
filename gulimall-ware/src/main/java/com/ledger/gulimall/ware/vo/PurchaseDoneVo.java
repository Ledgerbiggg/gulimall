package com.ledger.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;

    private List<PurChaseItemDoneVo> items;

}
