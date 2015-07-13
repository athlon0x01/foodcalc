package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.Product;

import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
public interface IProductRepo {

    List<ProductCategory> getCategories();

    List<Product> getAllProducts();
}
