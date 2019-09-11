package com.outdoor.foodcalc.model.product;

import java.util.List;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} with embedded {@link com.outdoor.foodcalc.domain.model.product.Product}.
 *
 * @author Anton Borovyk
 */
public class CategoryWithProducts {
    public long id;
    public String name;
    public List<SimpleProduct> products;
}
