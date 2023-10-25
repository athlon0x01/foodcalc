package com.outdoor.foodcalc.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} with embedded {@link com.outdoor.foodcalc.domain.model.product.Product}.
 *
 * @author Anton Borovyk
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryWithProducts {
    private long id;
    private String name;
    private List<ProductView> products;
}
