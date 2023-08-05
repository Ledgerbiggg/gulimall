package com.ledger.gulimall.product.entity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ledger.gulimall.product.entity.CategoryEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author ledger
 * @version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryEntityDTO extends CategoryEntity {
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<CategoryEntityDTO> children;

}
