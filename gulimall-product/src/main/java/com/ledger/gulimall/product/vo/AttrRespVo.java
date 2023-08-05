package com.ledger.gulimall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@Data
public class AttrRespVo extends AttrVo{

    private String catelogName;

    private String groupName;

    private List<Long> catelogPath;


}
