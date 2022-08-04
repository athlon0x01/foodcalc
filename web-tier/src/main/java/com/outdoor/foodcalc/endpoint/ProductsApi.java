package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Products REST API and swagger documentation.
 *
 * @author Anton Borovyk.
 */
@RequestMapping("${spring.data.rest.basePath}/products")
@Api(tags = "Products")
public interface ProductsApi {

    @ApiOperation(value = "Get all products grouped by categories",
        response = CategoryWithProducts.class,
        responseContainer = "List"
    )
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "All products returned",
                response = CategoryWithProducts.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<CategoryWithProducts> getAllProducts();

    @ApiOperation(value = "Get product by ID",
            response = ProductView.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Requested product returned", response = ProductView.class),
            @ApiResponse(code = 404, message = "Product not found")
    })
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    ProductView getProduct(@ApiParam(value = "ID of the product", required = true)
                           @PathVariable("id") long id);

    @ApiOperation(value = "Create a new product",
            notes = "Creates a new product and returns this entity",
            response = ProductView.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "New product created", response = ProductView.class),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ProductView addProduct(@ApiParam(value = "Product", required = true)
                           @RequestBody ProductView product);

    @ApiOperation(value = "Update product by ID",
            notes = "Updates product and returns updated value",
            response = ProductView.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product updated", response = ProductView.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Product not found")
    })
    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    ProductView updateProduct(@ApiParam(value = "ID of the product", required = true)
                              @PathVariable("id") long id,
                              @ApiParam(value = "Product", required = true)
                              @RequestBody ProductView product);

    @ApiOperation(value = "Delete product by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Product deleted"),
            @ApiResponse(code = 404, message = "Product not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProduct(@ApiParam(value = "ID of the Product", required = true)
                       @PathVariable("id") long id);
}
