package com.outdoor.foodcalc.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} class.
 *
 * @author Anton Borovyk.
 */
@AllArgsConstructor
@NoArgsConstructor
public @Data class SimpleProductCategory {

    private long id;

    @NotEmpty
    private String name;

}
