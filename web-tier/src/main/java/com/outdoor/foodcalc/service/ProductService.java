package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import com.outdoor.foodcalc.model.product.ProductCategoryView;
import com.outdoor.foodcalc.model.product.ProductItem;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link Product} objects,
 * including transformation domain objects to view models and vise versa.
 *
 * @author Anton Borovyk
 */
@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductDomainService productDomainService;
    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductService(ProductDomainService productDomainService, ProductCategoryService productCategoryService) {
        this.productDomainService = productDomainService;
        this.productCategoryService = productCategoryService;
    }

    /**
     * Gets all {@link Product} objects mapped to {@link ProductView}.
     *
     * @return list of products
     */
    public List<CategoryWithProducts> getAllProducts() {
        //load products & categories
        final List<ProductCategoryView> categories = productCategoryService.getCategories();
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
            ProductCategoryView category, Map<Long, List<Product>> productsMap) {
        final List<Product> productList = productsMap.get(category.getId());
        return CategoryWithProducts.builder()
                .id(category.getId())
                .name(category.getName())
                .products((productList == null) ? new ArrayList<>() : productList.stream()
                        .map(this::mapProduct)
                        .collect(Collectors.toList()))
                .build();
    }

    ProductRef buildMockProduct(ProductItem product) {
        return new ProductRef(Product.builder()
                .productId(product.getProductId())
                .build(),
                product.getWeight());
    }

    List<ProductRef> buildMockProducts(List<ProductItem> products) {
        return products.stream()
                .map(this::buildMockProduct)
                .collect(Collectors.toList());
    }

    /**
     * Gets all {@link Product} objects mapped to {@link ProductView}.
     *
     * @param id product Id to load
     * @return loaded product
     */
    public ProductView getProduct(long id) {
        return productDomainService.getProduct(id)
                .map(this::mapProduct)
                .orElseThrow(() -> new NotFoundException("Failed to get Product, id = " + id));
    }

    /**
     * Adds new {@link Product}.
     *
     * @param view product to add
     * @return new product
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public ProductView addProduct(ProductView view) {
        Product productToAdd = Product.builder()
                .name(view.getName())
                .category(new ProductCategory(view.getCategoryId(), ""))
                .calorific(view.getCalorific())
                .proteins(view.getProteins())
                .fats(view.getFats())
                .carbs(view.getCarbs())
                .defaultWeight(Math.round(view.getWeight() *10))
                .build();

        return mapProduct(productDomainService.addProduct(productToAdd));
    }

    /**
     * Updates selected {@link Product} with new value.
     *
     * @param productModel updated
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateProduct(ProductView productModel) {
        Product productToUpdate = Product.builder()
                .productId(productModel.getId())
                .name(productModel.getName())
                .category(new ProductCategory(productModel.getCategoryId(), ""))
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
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteProduct(long id) {
        productDomainService.deleteProduct(id);
    }

    private ProductView mapProduct(Product product) {
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

    ProductView mapProductRef(ProductRef product) {
        return ProductView.builder()
                .id(product.getProductId())
                .name(product.getName())
                .categoryId(product.getProductCategoryId())
                .calorific(product.getCalorific())
                .proteins(product.getProteins())
                .fats(product.getFats())
                .carbs(product.getCarbs())
                .weight(product.getWeight())
                .build();
    }
}
