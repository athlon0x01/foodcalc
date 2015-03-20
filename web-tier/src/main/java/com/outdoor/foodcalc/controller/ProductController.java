package com.outdoor.foodcalc.controller;

import com.outdoor.foodcalc.model.CategoryModel;
import com.outdoor.foodcalc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
@Controller
public class ProductController {

    @Autowired
    ProductService productService;

    @RequestMapping("/products")
    public String allProducts(ModelMap model) {
        List<CategoryModel> categories = productService.getAllProducts();
        model.addAttribute("categories", categories);
        return "products";
    }
}
