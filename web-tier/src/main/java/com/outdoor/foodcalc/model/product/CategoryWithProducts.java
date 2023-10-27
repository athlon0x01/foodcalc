package com.outdoor.foodcalc.model.product;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} with embedded {@link com.outdoor.foodcalc.domain.model.product.Product}.
 *
 * @author Anton Borovyk
 */
@Data
@Jacksonized
@Builder
public class CategoryWithProducts {
    private long id;
    private String name;
    private List<ProductView> products;
}
