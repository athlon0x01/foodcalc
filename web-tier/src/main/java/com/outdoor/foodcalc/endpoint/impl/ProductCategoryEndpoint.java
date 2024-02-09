package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.ProductCategoriesApi;
import com.outdoor.foodcalc.model.product.ProductCategoryView;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * REST Endpoint for Product Category related operations
 *
 * @author Anton Borovyk.
 */
@RestController
public class ProductCategoryEndpoint extends AbstractEndpoint implements ProductCategoriesApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryEndpoint.class);

    private final ProductCategoryService categoryService;

    @Autowired
    public ProductCategoryEndpoint(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<ProductCategoryView> getCategories() {
        LOG.debug("Getting all product categories");
        return categoryService.getCategories();
    }

    public ProductCategoryView getCategory(@PathVariable("id") long id) {
        LOG.debug("Getting product category id = {}", id);
        return categoryService.getCategory(id);
    }

    public ProductCategoryView addCategory(@RequestBody @Valid ProductCategoryView category) {
        LOG.debug("Adding new product category - {}", category);
        return categoryService.addCategory(category.getName());
    }

    public void updateCategory(@PathVariable("id") long id,
                               @RequestBody @Valid ProductCategoryView category) {
        verifyEntityId(id, category);
        LOG.debug("Updating product category {}", category);
        categoryService.updateCategory(category);
    }

    public void deleteCategory(@PathVariable("id") long id) {
        LOG.debug("Deleting product category id = {}", id);
        categoryService.deleteCategory(id);
    }
}
