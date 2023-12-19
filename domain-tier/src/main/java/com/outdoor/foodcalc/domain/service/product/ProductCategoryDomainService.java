package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryDomainService.class);

    private final IProductCategoryRepo categoryRepo;
    private final IProductRepo productRepo;

    @Autowired
    public ProductCategoryDomainService(IProductCategoryRepo categoryRepo, IProductRepo productRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
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
     * @throws NotFoundException If product category doesn't exist
     * @throws FoodcalcDomainException If product category wasn't updated
     */
    public void updateCategory(ProductCategory category) {
        if (!categoryRepo.exist(category.getCategoryId())){
            LOG.error("Product category with id={} doesn't exist", category.getCategoryId());
            throw new NotFoundException("Product category doesn't exist");
        }
        if(!categoryRepo.updateCategory(category)) {
            LOG.error("Failed to update product category with id=" + category.getCategoryId());
            throw new FoodcalcDomainException("Failed to update product category with id=" + category.getCategoryId());
        }
    }

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param id category Id to delete
     * @throws NotFoundException If product category doesn't exist
     * @throws FoodcalcDomainException If product category wasn't deleted
     * @throws IllegalArgumentException If product category isn't empty
     */
    public void deleteCategory(long id) {
        if (!categoryRepo.exist(id)){
            LOG.error("Product category with id={} doesn't exist", id);
            throw new NotFoundException("Product category doesn't exist");
        }
        if (productRepo.countProductsInCategory(id) == 0) {
            if (!categoryRepo.deleteCategory(id)) {
                LOG.error("Failed to delete product category with id=" + id);
                throw new FoodcalcDomainException("Failed to delete product category with id=" + id);
            }
        } else {
            LOG.error("Product category with id = {} is not empty", id);
            throw new IllegalArgumentException("Product category with id=" + id + " is not empty");
        }
    }
}
