package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST Endpoint for Product Category related operations
 *
 * @author Anton Borovyk.
 */
@RestController
@RequestMapping("${spring.data.rest.basePath}/product-categories")
public class ProductCategoryEndpoint {

    private ProductCategoryService categoryService;

    @Autowired
    public ProductCategoryEndpoint(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @RequestMapping(produces = APPLICATION_JSON_VALUE)
    public List<SimpleProductCategory> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping
    @RequestMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleProductCategory> getCategory(@PathVariable("id") long id) {
        Optional<SimpleProductCategory> category = categoryService.getCategory(id);
        if (!category.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category.get(), HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleProductCategory addCategory(@RequestBody SimpleProductCategory category) {
        return categoryService.addCategory(category.name);
    }

    @PutMapping
    @RequestMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleProductCategory> updateCategory(@RequestBody SimpleProductCategory category) {
        if (!categoryService.updateCategory(category)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping
    @RequestMapping("/{id}")
    public ResponseEntity<SimpleProductCategory> deleteCategory(@PathVariable("id") long id) {
        if(!categoryService.deleteCategory(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
