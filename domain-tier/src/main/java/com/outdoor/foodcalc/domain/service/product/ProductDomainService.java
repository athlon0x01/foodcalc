package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProductDomainService(IProductRepo productRepo) {
        this.productRepo = productRepo;
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
        long id = productRepo.addProduct(product);
        return  Product.builder().productId(id).name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory()).calorific(product.getCalorific())
                .proteins(product.getProteins()).fats(product.getFats())
                .carbs(product.getCarbs()).defaultWeight(product.getDefaultWeightInt()).build();
    }

    /**
     * Updates selected {@link Product} with new value.
     *
     * @param product to update
     * @return if product was updated
     */
    public boolean updateProduct(Product product) {
        if(!productRepo.existsProduct(product.getProductId())) {
            LOG.error("Product with id={} doesn't exist", product.getProductId());
            throw new NotFoundException("Product doesn't exist");
        }
        return productRepo.updateProduct(product);
    }

    /**
     * Deletes selected {@link Product}.
     *
     * @param id product to delete
     * @return if product was deleted
     */
    public boolean deleteProduct(long id) {
        if(!productRepo.existsProduct(id)) {
            LOG.error("Product with id={} doesn't exist", id);
            throw new NotFoundException("Product doesn't exist");
        }
        return productRepo.deleteProduct(id);
    }
}
