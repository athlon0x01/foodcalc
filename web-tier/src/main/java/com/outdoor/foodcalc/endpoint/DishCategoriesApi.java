package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.dish.DishCategoryView;
import com.outdoor.foodcalc.model.product.ProductCategoryView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("${spring.data.rest.basePath}/dish-categories")
public interface DishCategoriesApi {

    @ApiOperation(value = "Get all dish categories",
            response = DishCategoryView.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All dish categories returned", response = DishCategoryView.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<DishCategoryView> getDishCategories();

    @ApiOperation(value = "Get dish category by id = {}",
            response = DishCategoryView.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Requested dish category returned", response = DishCategoryView.class),
            @ApiResponse(code = 404, message = "Dish category not found")
    })
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    DishCategoryView getDishCategory(@PathVariable("id") long id);

    @ApiOperation(value = "Add new dish category - {}",
            response = DishCategoryView.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "New dish category created", response = ProductCategoryView.class),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    DishCategoryView addDishCategory(@RequestBody DishCategoryView category);

    @ApiOperation(value = "Update dish category by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Dish category updated"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Dish category not found")
    })
    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateDishCategory(@ApiParam(value = "ID of the Dish category", required = true)
                            @PathVariable("id") long id,
                            @ApiParam(value = "Dish category", required = true)
                            @RequestBody DishCategoryView category);

    @ApiOperation(value = "Delete dish category by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Dish category deleted"),
            @ApiResponse(code = 404, message = "Dish category not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDishCategory(@ApiParam(value = "ID of the Dish category", required = true)
                            @PathVariable("id") long id);
}
