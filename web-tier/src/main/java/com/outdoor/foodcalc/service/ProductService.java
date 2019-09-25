package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.SimpleProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class ProductService {

    private ProductDomainService productDomainService;

    private ProductCategoryDomainService productCategoryDomainService;

    @Autowired
    public ProductService(ProductDomainService productDomainService, ProductCategoryDomainService productCategoryDomainService) {
        this.productDomainService = productDomainService;
        this.productCategoryDomainService = productCategoryDomainService;
    }

    public List<CategoryWithProducts> getAllProducts() {
        //load products & categories
        final List<ProductCategory> categories = productCategoryDomainService.getCategories();
        final List<Product> products = productDomainService.getAllProducts();
        //group products by categories
        final Map<Long, List<Product>> productsMap = products.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().getCategoryId()));
        //map domain classes to UI model
        return categories.stream()
                .map(c -> {
                    final CategoryWithProducts cm = new CategoryWithProducts();
                    cm.id = c.getCategoryId();
                    cm.name = c.getName();
                    final List<Product> productList = productsMap.get(c.getCategoryId());
                    cm.products = (productList == null) ? new ArrayList<>() : productList.stream()
                            .map(p -> {
                                final SimpleProduct pm = new SimpleProduct();
                                pm.id = p.getProductId();
                                pm.name = p.getName();
                                pm.categoryId = c.getCategoryId();
                                pm.calorific = p.getCalorific();
                                pm.proteins = p.getProteins();
                                pm.fats = p.getFats();
                                pm.carbs = p.getCarbs();
                                pm.defaultWeight = p.getDefaultWeight();
                                return pm;
                            })
                            .collect(Collectors.toList());
                    return cm;
                })
                .collect(Collectors.toList());
    }
}
