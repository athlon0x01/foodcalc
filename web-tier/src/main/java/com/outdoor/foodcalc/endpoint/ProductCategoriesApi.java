package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Product categories REST API and swagger documentation
 *
 * @author Anton Borovyk.
 */
@RequestMapping("${spring.data.rest.basePath}/product-categories")
@Api(tags = "Product categories")
public interface ProductCategoriesApi {

    @ApiOperation(value = "Get all products categories",
        response = SimpleProductCategory.class,
        responseContainer = "List"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "All product categories returned", response = SimpleProductCategory.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<SimpleProductCategory> getCategories();


    @ApiOperation(value = "Get product category by ID",
        response = SimpleProductCategory.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Requested product category returned", response = SimpleProductCategory.class),
        @ApiResponse(code = 404, message = "Product category not found")
    })
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<SimpleProductCategory> getCategory(@ApiParam(value = "ID of the product category", required = true)
                                                      @PathVariable("id") long id);


    @ApiOperation(value = "Create new product category",
        notes = "Create new product category and returns this entity",
        response = SimpleProductCategory.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "New product category created", response = SimpleProductCategory.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 404, message = "Product category not found")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    SimpleProductCategory addCategory(@ApiParam(value = "Product category", required = true)
                                      @RequestBody SimpleProductCategory category);


    @ApiOperation(value = "Update product category by ID",
        notes = "Updates product category and returns updated value",
        response = SimpleProductCategory.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Product category updated", response = SimpleProductCategory.class),
        @ApiResponse(code = 400, message = "Bad request"),
        @ApiResponse(code = 404, message = "Product category not found")
    })
    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ResponseEntity<SimpleProductCategory> updateCategory(@ApiParam(value = "ID of the product category", required = true)
                                                         @PathVariable("id") long id,
                                                         @ApiParam(value = "Product category", required = true)
                                                         @RequestBody SimpleProductCategory category);

    @ApiOperation(value = "Delete product category by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Product category deleted"),
        @ApiResponse(code = 404, message = "Product category not found")
    })
    @DeleteMapping("{id}")
    ResponseEntity<SimpleProductCategory> deleteCategory(@ApiParam(value = "ID of the product category", required = true)
                                                         @PathVariable("id") long id);
}
