package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
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

    private ProductDomainService productDomainService;

    private final ProductCategoryDomainService productCategoryDomainService;

    private final List<Product> products = new ArrayList<>();

    @Autowired
    public ProductService(ProductDomainService productDomainService, ProductCategoryDomainService productCategoryDomainService) {
        this.productDomainService = productDomainService;
        this.productCategoryDomainService = productCategoryDomainService;
    }

    //TODO remove it, temporary method for DishService
    Product getDomainProduct(long id) {
        final Optional<Product> first = products.stream()
                .filter(product -> product.getProductId() == id)
                .findFirst();
        return first.orElseThrow(() -> new NotFoundException(String.valueOf(id)));
    }

    public List<CategoryWithProducts> getAllProducts() {
        //load products & categories
        final List<ProductCategory> categories = productCategoryDomainService.getCategories();
//        final List<Product> products = productDomainService.getAllProducts();
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
                            .map(this::mapSimpleProduct)
                            .collect(Collectors.toList());
                    return cm;
                })
                .collect(Collectors.toList());
    }

    public ProductView getProduct(long id) {
        final Optional<ProductView> first = products.stream()
                .filter(product -> product.getProductId() == id)
                .map(this::mapSimpleProduct)
                .findFirst();
        return first.orElseThrow(() -> new NotFoundException(String.valueOf(id)));
    }

    public ProductView addProduct(ProductView product) {
        product.id = products.stream()
                .map(Product::getProductId)
                .max(Long::compareTo)
                .orElse((long) products.size())
                + 1;
        final ProductCategory category = productCategoryDomainService.getCategory(product.categoryId)
                .orElseThrow(() -> new NotFoundException("Failed to get Product Category, id = " + product.categoryId));
        final Product domainProduct = new Product(product.id,
                product.name,
                "",
                category,
                product.calorific,
                product.proteins,
                product.fats,
                product.carbs,
                Math.round(product.weight *10));
        products.add(domainProduct);
        return product;
    }

    public boolean updateProduct(ProductView product) {
        final Optional<Product> first = products.stream()
                .filter(p -> p.getProductId() == product.id)
                .findFirst();
        Product original = first.orElseThrow(() -> new NotFoundException(String.valueOf(product.id)));
        if (original.getCategory().getCategoryId() != product.categoryId) {
            final ProductCategory category = productCategoryDomainService.getCategory(product.categoryId)
                    .orElseThrow(() -> new NotFoundException("Failed to get Product Category, id = " + product.categoryId));
            original.setCategory(category);
        }
        original.setName(product.name);
        original.setCalorific(product.calorific);
        original.setProteins(product.proteins);
        original.setFats(product.fats);
        original.setCarbs(product.carbs);
        original.setDefaultWeight(product.weight);
        return true;
    }

    public boolean deleteProduct(long id) {
        int index = 0;
        while (index < products.size()) {
            if (products.get(index).getProductId() == id) {
                products.remove(index);
                return true;
            }
            index++;
        }
        return false;
    }

    private ProductView mapSimpleProduct(Product product) {
        final ProductView pm = new ProductView();
        pm.id = product.getProductId();
        pm.name = product.getName();
        pm.categoryId = product.getCategory().getCategoryId();
        pm.calorific = product.getCalorific();
        pm.proteins = product.getProteins();
        pm.fats = product.getFats();
        pm.carbs = product.getCarbs();
        pm.weight = product.getDefaultWeight();
        return pm;
    }
}
