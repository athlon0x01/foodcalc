package com.outdoor.foodcalc.model;

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

    public String getName() {
        return name;
    }

    public List<SimpleProduct> getProducts() {
        return products;
    }
}
