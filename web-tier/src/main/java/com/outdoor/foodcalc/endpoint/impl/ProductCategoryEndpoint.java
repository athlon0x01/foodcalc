package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.endpoint.ProductCategoriesApi;
import com.outdoor.foodcalc.model.product.SimpleProductCategory;
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

    public List<SimpleProductCategory> getCategories() {
        LOG.debug("Getting all product categories");
        return categoryService.getCategories();
    }

    public SimpleProductCategory getCategory(@PathVariable("id") long id) {
        LOG.debug("Getting product category id = {}", id);
        return categoryService.getCategory(id);
    }

    public SimpleProductCategory addCategory(@RequestBody @Valid SimpleProductCategory category) {
        LOG.debug("Adding new product category - {}", category);
        return categoryService.addCategory(category.name);
    }

    public SimpleProductCategory updateCategory(@PathVariable("id") long id,
                                                @RequestBody @Valid SimpleProductCategory category) {
        if (id != category.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, category.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + category.id);
        }
        LOG.debug("Updating product category {}", category);
        if (!categoryService.updateCategory(category)) {
            throw new FoodcalcException("Product category failed to update");
        }
        return category;
    }

    public void deleteCategory(@PathVariable("id") long id) {
        LOG.debug("Deleting product category id = {}", id);
        if (!categoryService.deleteCategory(id)) {
            throw new FoodcalcException("Product category failed to delete");
        }
    }
}
