package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.FoodcalcException;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.dish.CategoryWithDishes;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.dish.SimpleDish;
import com.outdoor.foodcalc.service.DishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/dishes")
public class DishEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(DishEndpoint.class);

    private final DishService dishService;

    @Autowired
    public DishEndpoint(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<CategoryWithDishes> getAllDishes() {
        LOG.debug("Getting all dishes");
        return dishService.getAllDishes();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public DishView getDish(@PathVariable("id") long id) {
        LOG.debug("Getting dish id = {}", id);
        return dishService.getDish(id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleDish addDish(@RequestBody @Valid SimpleDish dish) {
        LOG.debug("Adding new dish - {}", dish);
        return dishService.addDish(dish);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleDish updateDish(@PathVariable("id") long id,
                                   @RequestBody @Valid SimpleDish dish) {
        if (id != dish.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, dish.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + dish.id);
        }
        LOG.debug("Updating dish {}", dish);
        if (!dishService.updateDish(dish)) {
            throw new FoodcalcException("Product failed to update");
        }
        return dish;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDish(@PathVariable("id") long id) {
        LOG.debug("Deleting dish id = {}", id);
        if (!dishService.deleteDish(id)) {
            throw new FoodcalcException("Product failed to delete");
        }
    }
}
