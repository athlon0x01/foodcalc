package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.product.ProductCategoryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link ProductCategory} objects,
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
     * Gets all {@link ProductCategory} objects mapped to {@link ProductCategoryView}.
     *
     * @return list of categories
     */
    public List<ProductCategoryView> getCategories() {
        return categoryDomainService.getCategories().stream()
            .map(this::mapProductCategory)
            .collect(Collectors.toList());
    }

    /**
     * Gets all {@link ProductCategory} objects mapped to {@link ProductCategoryView}.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public ProductCategoryView getCategory(long id) {
        Optional<ProductCategory> category = categoryDomainService.getCategory(id);
        if (!category.isPresent()) {
            LOG.error("Product category with id={} wasn't found", id);
            throw new NotFoundException("Product category wasn't found");
        }
        return mapProductCategory(category.get());
    }

    private ProductCategoryView mapProductCategory(ProductCategory productCategory) {
        return ProductCategoryView.builder()
                .id(productCategory.getCategoryId())
                .name(productCategory.getName())
                .build();
    }

    /**
     * Add new {@link ProductCategory}.
     *
     * @param categoryName name of new category
     * @return new category
     */
    public ProductCategoryView addCategory(String categoryName) {
        return mapProductCategory(
            categoryDomainService.addCategory(
                new ProductCategory(-1, categoryName)));
    }


    /**
     * Updates selected {@link ProductCategory} with new value.
     *
     * @param model updated category
     */
    public void updateCategory(ProductCategoryView model) {
        categoryDomainService.updateCategory(new ProductCategory(model.getId(), model.getName()));
    }

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param id category Id to delete
     */
    public void deleteCategory(long id) {
        categoryDomainService.deleteCategory(id);
    }
}
