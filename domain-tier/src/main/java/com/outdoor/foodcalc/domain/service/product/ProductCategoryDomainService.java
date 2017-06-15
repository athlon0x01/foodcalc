package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain service for all operations with {@link ProductCategory} objects.
 *
 * @author Anton Borovyk.
 */
@Service
public class ProductCategoryDomainService {

    private IProductCategoryRepo categoryRepo;

    @Autowired
    public ProductCategoryDomainService(IProductCategoryRepo productCategoryRepo) {
        this.categoryRepo = productCategoryRepo;
    }

    /**
     * Loads all {@link ProductCategory} objects.
     *
     * @return list of categories
     */
    public List<ProductCategory> getCategories() {
        return categoryRepo.getCategories();
    }

    /**
     * Add new {@link ProductCategory}.
     *
     * @param category category to add
     */
    boolean addCategory(ProductCategory category) {
        return categoryRepo.addCategory(category);
    }


    /**
     * Updates selected {@link ProductCategory} with new value.
     *
     * @param category updated category
     */
    boolean updateCategory(ProductCategory category) {
        return categoryRepo.updateCategory(category);
    }

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param category category to delete
     */
    boolean deleteCategory(ProductCategory category) {
        return categoryRepo.deleteCategory(category);
    }
}
