package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.product.ProductCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} objects,
 * including transformation domain objects to view models and vise versa.
 *
 * @author Anton Borovyk.
 */
@Service
public class ProductCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryService.class);

    private final ProductCategoryDomainService categoryDomainService;

    @Autowired
    public ProductCategoryService(ProductCategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    /**
     * Gets all {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} objects mapped to {@link ProductCategory}.
     *
     * @return list of categories
     */
    public List<ProductCategory> getCategories() {
        return categoryDomainService.getCategories().stream()
            .map(this::mapProductCategory)
            .collect(Collectors.toList());
    }

    /**
     * Gets all {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} objects mapped to {@link ProductCategory}.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public ProductCategory getCategory(long id) {
        Optional<com.outdoor.foodcalc.domain.model.product.ProductCategory> category = categoryDomainService.getCategory(id);
        if (!category.isPresent()) {
            LOG.error("Product category with id={} wasn't found", id);
            throw new NotFoundException("Product category wasn't found");
        }
        return mapProductCategory(category.get());
    }

    private ProductCategory mapProductCategory(com.outdoor.foodcalc.domain.model.product.ProductCategory productCategory) {
        return new ProductCategory(
                productCategory.getCategoryId(), productCategory.getName());
    }

    /**
     * Add new {@link com.outdoor.foodcalc.domain.model.product.ProductCategory}.
     *
     * @param categoryName name of new category
     * @return new category
     */
    public ProductCategory addCategory(String categoryName) {
        return mapProductCategory(
            categoryDomainService.addCategory(
                new com.outdoor.foodcalc.domain.model.product.ProductCategory(-1, categoryName)));
    }


    /**
     * Updates selected {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} with new value.
     *
     * @param model updated category
     */
    public void updateCategory(ProductCategory model) {
        categoryDomainService.updateCategory(new com.outdoor.foodcalc.domain.model.product.ProductCategory(model.getId(), model.getName()));
    }

    /**
     * Removes selected {@link com.outdoor.foodcalc.domain.model.product.ProductCategory}.
     *
     * @param id category Id to delete
     */
    public void deleteCategory(long id) {
        categoryDomainService.deleteCategory(id);
    }
}
