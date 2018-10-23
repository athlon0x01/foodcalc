package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
     * Loads {@link ProductCategory} object by Id.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public Optional<ProductCategory> getCategory(long id) {
        return categoryRepo.getCategory(id);
    }

    /**
     * Add new {@link ProductCategory}.
     *
     * @param category category to add
     * @return instance of new {@link ProductCategory}
     */
    public ProductCategory addCategory(ProductCategory category) {
        long id = categoryRepo.addCategory(category);
        return new ProductCategory(id, category.getName());
    }


    /**
     * Updates selected {@link ProductCategory} with new value.
     *
     * @param category updated category
     */
    public boolean updateCategory(ProductCategory category) {
        return categoryRepo.updateCategory(category);
    }

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param id category Id to delete
     */
    public boolean deleteCategory(long id) {
        return categoryRepo.deleteCategory(id);
    }
}
