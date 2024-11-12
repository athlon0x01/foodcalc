package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Domain service for all operations with {@link Product} objects.
 *
 * @author Anton Borovyk
 */
@Service
public class ProductDomainService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductDomainService.class);
    private final IProductRepo productRepo;
    private final ProductCategoryDomainService productCategoryService;

    public ProductDomainService(IProductRepo productRepo, ProductCategoryDomainService productCategoryService) {
        this.productRepo = productRepo;
        this.productCategoryService = productCategoryService;
    }

    /**
     * Loads all {@link Product} objects.
     *
     * @return list of products
     */
    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
    }

    /**
     * Loads {@link Product} object by Id.
     *
     * @param id product id
     * @return loaded product
     */
    public Optional<Product> getProduct(long id) {
        return productRepo.getProduct(id);
    }

    /**
     * Add new {@link Product}.
     *
     * @param product to add
     * @return new {@link Product} with auto generated Id
     */
    public Product addProduct(Product product) {
        final ProductCategory category = productCategoryService.getCategory(product.getCategory().getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Product Category, id = " + product.getCategory().getCategoryId()));
        product.setCategory(category);
        long id = productRepo.addProduct(product);
        return  product.toBuilder()
                .productId(id)
                .build();
    }

    /**
     * Updates selected {@link Product} with new value.
     *
     * @param product to update
     * @throws NotFoundException If product doesn't exist
     * @throws FoodcalcDomainException If product wasn't updated
     * */
    public void updateProduct(Product product) {
        if(!productRepo.existsProduct(product.getProductId())) {
            LOG.error("Product with id={} doesn't exist", product.getProductId());
            throw new NotFoundException("Product with id=" + product.getProductId() + " doesn't exist");
        }
        final ProductCategory category = productCategoryService.getCategory(product.getCategory().getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Product Category, id = " + product.getCategory().getCategoryId()));
        product.setCategory(category);

        if(!productRepo.updateProduct(product)) {
            throw new FoodcalcDomainException("Failed to update product with id=" + product.getProductId());
        }
    }

    /**
     * Deletes selected {@link Product}.
     *
     * @param id product to delete
     * @throws NotFoundException If product doesn't exist
     * @throws FoodcalcDomainException If product wasn't deleted
     * */
    public void deleteProduct(long id) {
        if(!productRepo.existsProduct(id)) {
            LOG.error("Product with id={} doesn't exist", id);
            throw new NotFoundException("Product doesn't exist");
        }
        if(!productRepo.deleteProduct(id)) {
            throw new FoodcalcDomainException("Failed to delete product with id=" + id);
        }
    }
}
