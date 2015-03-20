package com.outdoor.foodcalc.model;

import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public class CategoryModel {
    public String name;
    public List<ProductModel> products;

    public String getName() {
        return name;
    }

    public List<ProductModel> getProducts() {
        return products;
    }
}
