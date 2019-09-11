package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.ProductsApi;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Endpoint for Product related operations
 *
 * @author Anton Borovyk
 */
@RestController
public class ProductEndpoint implements ProductsApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProductEndpoint.class);

    private ProductService productService;

    @Autowired
    public ProductEndpoint(ProductService productService) {
        this.productService = productService;
    }

    public List<CategoryWithProducts> allProducts() {
        LOG.debug("Getting all products");
        return productService.getAllProducts();
    }
}
