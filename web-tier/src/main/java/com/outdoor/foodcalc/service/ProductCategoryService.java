package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.SimpleProductCategory;
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

    private ProductCategoryDomainService categoryDomainService;

    @Autowired
    public ProductCategoryService(ProductCategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    /**
     * Gets all {@link ProductCategory} objects mapped to {@link SimpleProductCategory}.
     *
     * @return list of categories
     */
    public List<SimpleProductCategory> getCategories() {
        return categoryDomainService.getCategories().stream()
            .map(this::mapProductCategory)
            .collect(Collectors.toList());
    }

    /**
     * Gets all {@link ProductCategory} objects mapped to {@link SimpleProductCategory}.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public Optional<SimpleProductCategory> getCategory(long id) {
        Optional<SimpleProductCategory> result = Optional.empty();
        Optional<ProductCategory> category = categoryDomainService.getCategory(id);
        if (category.isPresent()) {
            result = Optional.of(mapProductCategory(category.get()));
        }
        return result;
    }

    private SimpleProductCategory mapProductCategory(ProductCategory productCategory) {
        final SimpleProductCategory model = new SimpleProductCategory();
        model.id = productCategory.getCategoryId();
        model.name = productCategory.getName();
        return model;
    }

    /**
     * Add new {@link ProductCategory}.
     *
     * @param categoryName name of new category
     * @return new category
     */
    public SimpleProductCategory addCategory(String categoryName) {
        return mapProductCategory(
            categoryDomainService.addCategory(
                new ProductCategory(-1, categoryName)));
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
     * @param id category Id to delete
     */
    public boolean deleteCategory(long id) {
        return categoryDomainService.deleteCategory(id);
    }
}
