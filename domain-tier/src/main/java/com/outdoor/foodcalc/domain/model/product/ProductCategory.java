package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.IDomainEntity;

/**
 * Product categories, like bakery, meat, etc.
 *
 * @author Anton Borovyk
 */
public class ProductCategory implements IDomainEntity<ProductCategory> {

    private final int categoryId;
    private String name;

    public ProductCategory(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean sameIdentityAs(ProductCategory other) {
        return categoryId == other.categoryId;
    }
}
