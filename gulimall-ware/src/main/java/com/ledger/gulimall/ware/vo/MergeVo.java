package com.ledger.gulimall.ware.vo;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author ledger
 * @version 1.0
 **/

@Data
public class MergeVo {
    //整单id
    private Long purchaseId;
    //合并项集合
    private ArrayList<Long> items;

}
