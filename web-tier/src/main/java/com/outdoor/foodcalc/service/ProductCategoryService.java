package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.SimpleProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link ProductCategory} objects,
 * including transformation domain objects to view models and vise versa.
 *
 * @author Anton Borovyk.
 */
@Service
public class ProductCategoryService {

    private ProductCategoryDomainService categoryDomainService;

    @Autowired
    public ProductCategoryService(ProductCategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    /**
     * Gets all {@link ProductCategory} objects.
     *
     * @return list of categories
     */
    public List<SimpleProductCategory> getCategories() {
        return categoryDomainService.getCategories().stream()
            .map(category -> {
                final SimpleProductCategory model = new SimpleProductCategory();
                model.id = category.getCategoryId();
                model.name = category.getName();
                return model;
            })
            .collect(Collectors.toList());
    }

    /**
     * Add new {@link ProductCategory}.
     *
     * @param categoryName name of new category
     */
    public boolean addCategory(String categoryName) {
        return categoryDomainService.addCategory(new ProductCategory(-1, categoryName));
    }


    /**
     * Updates selected {@link ProductCategory} with new value.
     *
     * @param model updated category
     */
    public boolean updateCategory(SimpleProductCategory model) {
        return categoryDomainService.updateCategory(new ProductCategory(model.id, model.name));
    }

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param model category to delete
     */
    public boolean deleteCategory(SimpleProductCategory model) {
        return categoryDomainService.deleteCategory(new ProductCategory(model.id, model.name));
    }
}
