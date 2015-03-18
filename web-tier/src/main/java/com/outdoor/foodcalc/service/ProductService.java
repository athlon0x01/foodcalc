package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.product.Category;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
@Service
public class ProductService {

    @Autowired
    private ProductDomainService productDomainService;

    public List<Category> getAllProducts() {
        final List<Category> categories = productDomainService.getCategories();
        final List<Product> products = productDomainService.getAllProducts();
        //TODO convert domain objects to UI model
        return null;
    }
}
