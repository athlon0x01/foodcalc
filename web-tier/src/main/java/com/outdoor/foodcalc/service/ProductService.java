package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link Product} objects,
 * including transformation domain objects to view models and vise versa.
 *
 * @author Anton Borovyk
 */
@Service
public class ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);

    private final ProductDomainService productDomainService;

    private final ProductCategoryDomainService productCategoryDomainService;

    @Autowired
    public ProductService(ProductDomainService productDomainService, ProductCategoryDomainService productCategoryDomainService) {
        this.productDomainService = productDomainService;
        this.productCategoryDomainService = productCategoryDomainService;
    }

    /**
     * Gets all {@link Product} objects mapped to {@link ProductView}.
     *
     * @return list of products
     */
    public List<CategoryWithProducts> getAllProducts() {
        //load products & categories
        final List<ProductCategory> categories = productCategoryDomainService.getCategories();
        final List<Product> products = productDomainService.getAllProducts();

        //group products by categories
        final Map<Long, List<Product>> productsMap = products.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().getCategoryId()));

        //map domain classes to UI model
        return categories.stream()
                .map(category -> mapCategoryWithProducts(category, productsMap))
                .collect(Collectors.toList());
    }

    private CategoryWithProducts mapCategoryWithProducts(
            ProductCategory category, Map<Long, List<Product>> productsMap) {
        final List<Product> productList = productsMap.get(category.getCategoryId());
        return CategoryWithProducts.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                .products((productList == null) ? new ArrayList<>() : productList.stream()
                        .map(this::mapSimpleProduct)
                        .collect(Collectors.toList()))
                .build();
    }

    //TODO revert back to package scope
    public Product getDomainProduct(long id) {
        Optional<Product> domainProduct = productDomainService.getProduct(id);
        if(!domainProduct.isPresent()) {
            LOG.error("Product with id={} wasn't found", id);
            throw new NotFoundException("Product wasn't found");
        }
        return domainProduct.get();
    }

    /**
     * Gets all {@link Product} objects mapped to {@link ProductView}.
     *
     * @param id product Id to load
     * @return loaded product
     */
    public ProductView getProduct(long id) {
        Product domainProduct = getDomainProduct(id);
        return mapSimpleProduct(domainProduct);
    }

    /**
     * Adds new {@link Product}.
     *
     * @param simpleProduct product to add
     * @return new product
     */
    public ProductView addProduct(ProductView simpleProduct) {
        final ProductCategory category = productCategoryDomainService.getCategory(simpleProduct.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Product Category, id = " + simpleProduct.getCategoryId()));
        Product productToAdd = Product.builder()
                .productId(-1)
                .name(simpleProduct.getName())
                .category(category)
                .calorific(simpleProduct.getCalorific())
                .proteins(simpleProduct.getProteins())
                .fats(simpleProduct.getFats())
                .carbs(simpleProduct.getCarbs())
                .defaultWeight(Math.round(simpleProduct.getWeight() *10))
                .build();

        return mapSimpleProduct(productDomainService.addProduct(productToAdd));
    }

    /**
     * Updates selected {@link Product} with new value.
     *
     * @param productModel updated
     * @return if product updated
     */
    public void updateProduct(ProductView productModel) {
        final ProductCategory category = productCategoryDomainService.getCategory(productModel.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Product Category, id = " + productModel.getCategoryId()));
        Product productToUpdate = Product.builder()
                .productId(productModel.getId())
                .name(productModel.getName())
                .category(category)
                .calorific(productModel.getCalorific())
                .proteins(productModel.getProteins())
                .fats(productModel.getFats())
                .carbs(productModel.getCarbs())
                .defaultWeight(Math.round(productModel.getWeight() *10))
                .build();

        productDomainService.updateProduct(productToUpdate);
    }

    /**
     * Removes selected {@link Product}.
     *
     * @param id product Id to delete
     * @return if product deleted
     */
    public void deleteProduct(long id) {
        productDomainService.deleteProduct(id);
    }

    private ProductView mapSimpleProduct(Product product) {
        return ProductView.builder()
                .id(product.getProductId())
                .name(product.getName())
                .categoryId(product.getCategory().getCategoryId())
                .calorific(product.getCalorific())
                .proteins(product.getProteins())
                .fats(product.getFats())
                .carbs(product.getCarbs())
                .weight(product.getDefaultWeight())
                .build();
    }
}
