package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.meal.MealTypeView;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("${spring.data.rest.basePath}/meal-types")
public interface MealTypesApi {

    @ApiOperation(value = "Get all meal types",
                    response = MealTypeView.class,
                    responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All meal types returned",
                            response = MealTypeView.class, responseContainer = "List")
    })
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    List<MealTypeView> getMealTypes();

    @ApiOperation(value = "Get meal type by id = {}",
                    response = MealTypeView.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Requested meal type returned", response = MealTypeView.class),
            @ApiResponse(code = 404, message = "Meal type not found")
    })
    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    MealTypeView getMealType(@PathVariable("id") long id);

    @ApiOperation(value = "Add new meal type - {}",
                    response = MealTypeView.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "New meal type created", response = MealTypeView.class),
            @ApiResponse(code = 400, message = "Bad request")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    MealTypeView addMealType(@RequestBody MealTypeView mealTypeView);

    @ApiOperation(value = "Update meal type by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Meal type updated"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 404, message = "Meal type not found")
    })
    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateMealType(@ApiParam(value = "ID of the Meal type", required = true)
                        @PathVariable("id") long id,
                        @ApiParam(value = "Meal type", required = true)
                        @RequestBody MealTypeView mealTypeView);

    @ApiOperation(value = "Delete meal type by ID",
                    response = MealTypeView.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Meal type deleted"),
            @ApiResponse(code = 404, message = "Meal type not found")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMealType(@PathVariable("id") long id);
}
