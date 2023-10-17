package com.outdoor.foodcalc.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} with embedded {@link com.outdoor.foodcalc.domain.model.product.Product}.
 *
 * @author Anton Borovyk
 */
@AllArgsConstructor
@NoArgsConstructor
public @Data class CategoryWithProducts {
    private long id;
    private String name;
    private List<ProductView> products;
}
