package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.CategoryWithProducts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        @ApiResponse(code = 200, message = "All products returned", response = CategoryWithProducts.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<CategoryWithProducts> allProducts();
}
