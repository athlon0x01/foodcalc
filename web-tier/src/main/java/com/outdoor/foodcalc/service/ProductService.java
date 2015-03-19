package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.product.Category;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import com.outdoor.foodcalc.model.CategoryModel;
import com.outdoor.foodcalc.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
@Service
public class ProductService {

    @Autowired
    private ProductDomainService productDomainService;

    public List<CategoryModel> getAllProducts() {
        //load products & categories
        final List<Category> categories = productDomainService.getCategories();
        final List<Product> products = productDomainService.getAllProducts();
        //group products by categories
        final Map<Integer, List<Product>> productsMap = products.stream()
                .collect(Collectors.groupingBy(p -> p.getCategory().getCategoryId()));
        //map domain classes to UI model
        return categories.stream()
                .map(c -> {
                    final CategoryModel cm = new CategoryModel();
                    cm.name = c.getName();
                    cm.products = productsMap.get(c.getCategoryId()).stream()
                            .map(p -> {
                                final ProductModel pm = new ProductModel();
                                pm.name = p.getName();
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
