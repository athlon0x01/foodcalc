package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.product.ProductCategoryView;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
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
        response = ProductCategoryView.class,
        responseContainer = "List"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "All product categories returned", response = ProductCategoryView.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<ProductCategoryView> getCategories();


    @ApiOperation(value = "Get product category by ID",
        response = ProductCategoryView.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Requested product category returned", response = ProductCategoryView.class),
        @ApiResponse(code = 404, message = "Product category not found")
    })
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    ProductCategoryView getCategory(@ApiParam(value = "ID of the product category", required = true)
                                                      @PathVariable("id") long id);


    @ApiOperation(value = "Create new product category",
        notes = "Create new product category and returns this entity",
        response = ProductCategoryView.class
    )
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "New product category created", response = ProductCategoryView.class),
        @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ProductCategoryView addCategory(@ApiParam(value = "Product category", required = true)
                                      @RequestBody ProductCategoryView category);


    @ApiOperation(value = "Update product category by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Product category updated"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Product category not found")
    })
    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateCategory(@ApiParam(value = "ID of the product category", required = true)
                                         @PathVariable("id") long id,
                                         @ApiParam(value = "Product category", required = true)
                                         @RequestBody ProductCategoryView category);

    @ApiOperation(value = "Delete product category by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Product category deleted"),
            @ApiResponse(code = 404, message = "Product category not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategory(@ApiParam(value = "ID of the product category", required = true)
                        @PathVariable("id") long id);
}
