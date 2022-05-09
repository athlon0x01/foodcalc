package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;

import java.util.List;
import java.util.Optional;

/**
 * Product repository responsible for {@link Product} persistence.
 *
 * @author Anton Borovyk & Olga Borovyk
 */
public interface IProductRepo {

    /**
     * Loads all {@link Product}.
     *
     * @return list of products
     */
    List<Product> getAllProducts();

    /**
     * Count products number in the product category
     * @param categoryId product category Id
     * @return products number
     */
    long countProductsInCategory(long categoryId);

    /**
     * Loads {@link Product} object by Id.
     *
     * @param id product id
     * @return loaded product
     */
    Optional<Product> getProduct(long id);

    /**
     * Add new {@link Product}.
     *
     * @param product to add
     * @return auto generated Id
     */
    long addProduct(Product product);

    /**
     * Updates selected {@link Product} with new value.
     *
     * @param product to update
     * @return if product was updated
     */
    boolean updateProduct(Product product);

    /**
     * Deletes selected {@link Product}.
     *
     * @param id product to delete
     * @return if product was deleted
     */
    boolean deleteProduct(long id);

    /**
     * Check if specified {@link Product} exists.
     *
     * @param id product to check
     * @return if product exists
     */
    boolean existsProduct(long id);
}
