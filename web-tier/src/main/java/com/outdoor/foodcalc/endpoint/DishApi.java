package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.dish.CategoryWithDishes;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.dish.SimpleDish;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Dishes REST API and swagger documentation.
 *
 * @author Olga Borovyk.
 */
@RequestMapping("${spring.data.rest.basePath}/dishes")
public interface DishApi {

    @ApiOperation(value = "Get all dishes grouped by categories",
            response = CategoryWithDishes.class,
            responseContainer = "List"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All dishes returned",
            response = CategoryWithDishes.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<CategoryWithDishes> getAllDishes();

    @ApiOperation(value = "Get dish by ID",
            response = DishView.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Requested dish returned", response = DishView.class),
            @ApiResponse(code = 404, message = "Dish not found")
    })
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    DishView getDish(@ApiParam(value =  "ID of the dish", required = true)
                     @PathVariable("id") long id);

    @ApiOperation(value = "Create a new dish",
            notes = "Creates a new dish and returns this entity",
            response = SimpleDish.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "New dish created", response = SimpleDish.class),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    SimpleDish addDish(@ApiParam(value = "Dish", required = true)
                       @RequestBody @Valid SimpleDish dish);

    @ApiOperation(value = "Update dish by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Dish updated"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Dish not found")
    })
    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    void updateDish(@ApiParam(value = "ID of the Dish", required = true)
                          @PathVariable("id") long id,
                          @ApiParam(value = "Dish", required = true)
                          @RequestBody @Valid SimpleDish dish);

    @ApiOperation(value = "Delete dish by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Dish deleted"),
            @ApiResponse(code = 404, message = "Dish not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDish(@ApiParam(value = "ID of the Dish", required = true)
                    @PathVariable("id") long id);
}
