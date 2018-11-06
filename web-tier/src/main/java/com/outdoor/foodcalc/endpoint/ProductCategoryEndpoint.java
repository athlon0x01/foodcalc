package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryEndpoint.class);
    private static final String CATEGORY_ID_WAS_NOT_FOUND = "Product category id = {} was not found!";

    private ProductCategoryService categoryService;

    @Autowired
    public ProductCategoryEndpoint(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<SimpleProductCategory> getCategories() {
        LOG.info("Getting all product categories");
        return categoryService.getCategories();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleProductCategory> getCategory(@PathVariable("id") long id) {
        LOG.info("Getting product category id = {}", id);
        Optional<SimpleProductCategory> category = categoryService.getCategory(id);
        if (!category.isPresent()) {
            LOG.warn(CATEGORY_ID_WAS_NOT_FOUND, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category.get(), HttpStatus.OK);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleProductCategory addCategory(@RequestBody @Valid SimpleProductCategory category) {
        LOG.info("Adding new product category");
        return categoryService.addCategory(category.name);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleProductCategory> updateCategory(@PathVariable("id") long id,
                                                                @RequestBody @Valid SimpleProductCategory category) {
        if (id != category.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, category.id);
            throw new ValidationException("Path variable Id = " + id
                + " doesn't match with request body Id = " + category.id);
        }
        LOG.info("Updating product category id = {}", id);
        if (!categoryService.updateCategory(category)){
            LOG.warn(CATEGORY_ID_WAS_NOT_FOUND, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<SimpleProductCategory> deleteCategory(@PathVariable("id") long id) {
        LOG.info("Deleting product category id = {}", id);
        if(!categoryService.deleteCategory(id)) {
            LOG.warn(CATEGORY_ID_WAS_NOT_FOUND, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
