package com.outdoor.foodcalc.controller;

import com.outdoor.foodcalc.model.CategoryModel;
import com.outdoor.foodcalc.model.ProductModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
@Controller
public class ProductController {

    @RequestMapping("/products")
    public String allProducts(ModelMap model) {
        List<CategoryModel> categories = new ArrayList<>();
        CategoryModel c2 = new CategoryModel();
        c2.name = "Test category";

        List<ProductModel> products = new ArrayList<>();
        ProductModel p1 = new ProductModel();
        p1.name = "Test product 1";
        p1.calorific = 123.5f;
        p1.defaultWeight = 20.0f;
        products.add(p1);
        ProductModel p2 = new ProductModel();
        p2.name = "Test product 2";
        p2.calorific = 123.5f;
        p2.proteins = 21.4f;
        p2.fats = 11.4f;
        p2.carbs = 1.7f;
        products.add(p2);
        c2.products = products;
        categories.add(c2);

        CategoryModel c1 = new CategoryModel();
        c1.name = "Empty category";
        categories.add(c1);

        model.addAttribute("categories", categories);
        return "products";
    }
}
