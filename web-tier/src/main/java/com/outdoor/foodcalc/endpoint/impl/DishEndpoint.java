package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.DishApi;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.dish.CategoryWithDishes;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.dish.SimpleDish;
import com.outdoor.foodcalc.service.DishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class DishEndpoint implements DishApi {

    private static final Logger LOG = LoggerFactory.getLogger(DishEndpoint.class);

    private final DishService dishService;

    @Autowired
    public DishEndpoint(DishService dishService) {
        this.dishService = dishService;
    }

    public List<CategoryWithDishes> getAllDishes() {
        LOG.debug("Getting all dishes");
        return dishService.getAllDishes();
    }

    public DishView getDish(@PathVariable("id") long id) {
        LOG.debug("Getting dish id = {}", id);
        return dishService.getDish(id);
    }

    public SimpleDish addDish(@RequestBody @Valid SimpleDish dish) {
        LOG.debug("Adding new dish - {}", dish);
        return dishService.addDish(dish);
    }

    public void updateDish(@PathVariable("id") long id,
                                   @RequestBody @Valid SimpleDish dish) {
        if (id != dish.getId()) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, dish.getId());
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + dish.getId());
        }
        LOG.debug("Updating dish {}", dish);
        dishService.updateDish(dish);
    }

    public void deleteDish(@PathVariable("id") long id) {
        LOG.debug("Deleting dish id = {}", id);
        dishService.deleteDish(id);
    }
}
