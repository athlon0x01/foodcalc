package com.outdoor.foodcalc.domain.service.product;

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
     */
    public boolean updateCategory(ProductCategory category) {
        if (!categoryRepo.exist(category.getCategoryId())){
            LOG.error("Product category with id={} doesn't exist", category.getCategoryId());
            throw new NotFoundException("Product category doesn't exist");
        }
        return categoryRepo.updateCategory(category);
    }

    /**
     * Removes selected {@link ProductCategory}.
     *
     * @param id category Id to delete
     */
    public boolean deleteCategory(long id) {
        if (!categoryRepo.exist(id)){
            LOG.error("Product category with id={} doesn't exist", id);
            throw new NotFoundException("Product category doesn't exist");
        }
        if (productRepo.countProductsInCategory(id) == 0) {
            return categoryRepo.deleteCategory(id);
        } else {
            LOG.error("Product category with id = {} is not empty", id);
            throw new IllegalArgumentException("Product category is not empty");
        }
    }
}
