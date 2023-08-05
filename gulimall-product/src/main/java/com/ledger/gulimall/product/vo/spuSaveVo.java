/**
  * Copyright 2023 bejson.com 
  */
package com.ledger.gulimall.product.vo;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2023-06-29 16:47:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class spuSaveVo {

    private String spuName;
    private String spuDescription;
    private Long catalogId;
    private Long brandId;
    private int weight;
    private int publishStatus;
    private List<String> decript;
    private List<String> images;
    private Bounds bounds;
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;

}