package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.ProductCategoriesApi;
import com.outdoor.foodcalc.model.product.ProductCategory;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Endpoint for Product Category related operations
 *
 * @author Anton Borovyk.
 */
@RestController
public class ProductCategoryEndpoint implements ProductCategoriesApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryEndpoint.class);

    private final ProductCategoryService categoryService;

    @Autowired
    public ProductCategoryEndpoint(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<ProductCategory> getCategories() {
        LOG.debug("Getting all product categories");
        return categoryService.getCategories();
    }

    public ProductCategory getCategory(@PathVariable("id") long id) {
        LOG.debug("Getting product category id = {}", id);
        return categoryService.getCategory(id);
    }

    public ProductCategory addCategory(@RequestBody @Valid ProductCategory category) {
        LOG.debug("Adding new product category - {}", category);
        return categoryService.addCategory(category.getName());
    }

    public void updateCategory(@PathVariable("id") long id,
                                                @RequestBody @Valid ProductCategory category) {
        if (id != category.getId()) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, category.getId());
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + category.getId());
        }
        LOG.debug("Updating product category {}", category);
        categoryService.updateCategory(category);
    }

    public void deleteCategory(@PathVariable("id") long id) {
        LOG.debug("Deleting product category id = {}", id);
        categoryService.deleteCategory(id);
    }
}
