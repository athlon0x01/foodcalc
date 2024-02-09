package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.ProductsApi;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import com.outdoor.foodcalc.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Endpoint for Product related operations
 *
 * @author Anton Borovyk
 */
@RestController
public class ProductEndpoint extends AbstractEndpoint implements ProductsApi {

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

    public void updateProduct(@PathVariable("id") long id,
                              @RequestBody @Valid ProductView product) {
        verifyEntityId(id, product);
        LOG.debug("Updating product {}", product);
        productService.updateProduct(product);
    }

    public void deleteProduct(@PathVariable("id") long id) {
        LOG.debug("Deleting meal type id = {}", id);
        productService.deleteProduct(id);
    }
}
