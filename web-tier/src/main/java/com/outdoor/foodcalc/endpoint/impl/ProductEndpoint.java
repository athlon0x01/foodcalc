package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.endpoint.ProductsApi;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.SimpleProduct;
import com.outdoor.foodcalc.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    public List<CategoryWithProducts> allProducts() {
        LOG.debug("Getting all products");
        return productService.getAllProducts();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public SimpleProduct getProduct(@PathVariable("id") long id) {
        LOG.debug("Getting product id = {}", id);
        return productService.getProduct(id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleProduct addProduct(@RequestBody @Valid SimpleProduct product) {
        LOG.debug("Adding new product - {}", product);
        return productService.addProduct(product);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleProduct updateProduct(@PathVariable("id") long id,
                                   @RequestBody @Valid SimpleProduct product) {
        if (id != product.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, product.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + product.id);
        }
        LOG.debug("Updating product {}", product);
        if (!productService.updateProduct(product)) {
            throw new FoodcalcException("Product failed to update");
        }
        return product;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("id") long id) {
        LOG.debug("Deleting meal type id = {}", id);
        if (!productService.deleteProduct(id)) {
            throw new FoodcalcException("Product failed to delete");
        }
    }
}
