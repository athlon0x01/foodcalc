package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import com.outdoor.foodcalc.model.product.SimpleProductCategory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("${spring.data.rest.basePath}/dish-categories")
public interface DishCategoriesApi {

    @ApiOperation(value = "Get all dish categories",
            response = SimpleDishCategory.class,
            responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All dish categories returned", response = SimpleDishCategory.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<SimpleDishCategory> getDishCategories();

    @ApiOperation(value = "Get dish category by id = {}",
            response = SimpleDishCategory.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Requested dish category returned", response = SimpleDishCategory.class),
            @ApiResponse(code = 404, message = "Dish category not found")
    })
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    SimpleDishCategory getDishCategory(@PathVariable("id") long id);

    @ApiOperation(value = "Add new dish category - {}",
            response = SimpleDishCategory.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "New dish category created", response = SimpleProductCategory.class),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    SimpleDishCategory addDishCategory(@RequestBody SimpleDishCategory category);

    @ApiOperation(value = "Update dish category by ID",
            response = SimpleDishCategory.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dish category updated", response = SimpleProductCategory.class),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Dish category not found")
    })
    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    SimpleDishCategory updateDishCategory(@PathVariable("id") long id,
                                          @RequestBody SimpleDishCategory category);

    @ApiOperation(value = "Delete dish category by ID",
            response = SimpleDishCategory.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Dish category deleted"),
            @ApiResponse(code = 404, message = "Dish category not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDishCategory(@PathVariable("id") long id);
}
