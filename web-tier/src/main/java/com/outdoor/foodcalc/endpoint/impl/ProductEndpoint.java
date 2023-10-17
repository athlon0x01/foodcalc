package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.endpoint.ProductsApi;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import com.outdoor.foodcalc.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Endpoint for Product related operations
 *
 * @author Anton Borovyk
 */
@RestController
public class ProductEndpoint implements ProductsApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProductEndpoint.class);

    private final ProductService productService;

    @Autowired
    public ProductEndpoint(ProductService productService) {
        this.productService = productService;
    }

    public List<CategoryWithProducts> getAllProducts() {
        LOG.debug("Getting all products");
        return productService.getAllProducts();
    }

    public ProductView getProduct(@PathVariable("id") long id) {
        LOG.debug("Getting product id = {}", id);
        return productService.getProduct(id);
    }

    public ProductView addProduct(@RequestBody @Valid ProductView product) {
        LOG.debug("Adding new product - {}", product);
        return productService.addProduct(product);
    }

    public ProductView updateProduct(@PathVariable("id") long id,
                                     @RequestBody @Valid ProductView product) {
        if (id != product.getId()) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, product.getId());
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + product.getId());
        }
        LOG.debug("Updating product {}", product);
        if (!productService.updateProduct(product)) {
            throw new FoodcalcException("Product failed to update");
        }
        return product;
    }

    public void deleteProduct(@PathVariable("id") long id) {
        LOG.debug("Deleting meal type id = {}", id);
        if (!productService.deleteProduct(id)) {
            throw new FoodcalcException("Product failed to delete");
        }
    }
}
