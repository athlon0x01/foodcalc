package com.outdoor.foodcalc.controller;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Spring MVC controller for Product related operations
 *
 * @author Anton Borovyk.
 */
@Controller
public class ProductCategoryController {

    private ProductCategoryService service;

    @Autowired
    public ProductCategoryController(ProductCategoryService service) {
        this.service = service;
    }

    @RequestMapping("/categories")
    public String allCategories(ModelMap model) {
        List<SimpleProductCategory> categories = service.getCategories();
        model.addAttribute("categories", categories);
        return "categories";
    }
}
