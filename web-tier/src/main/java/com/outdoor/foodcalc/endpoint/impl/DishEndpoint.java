package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.endpoint.DishApi;
import com.outdoor.foodcalc.model.dish.CategoryWithDishes;
import com.outdoor.foodcalc.model.dish.DishInfo;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.service.DishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class DishEndpoint extends AbstractEndpoint implements DishApi {

    private static final Logger LOG = LoggerFactory.getLogger(DishEndpoint.class);

    private final DishService dishService;

    @Autowired
    public DishEndpoint(DishService dishService) {
        this.dishService = dishService;
    }

    public List<CategoryWithDishes> getAllDishes() {
        LOG.debug("Getting all dishes");
        return dishService.getAllTemplateDishes();
    }

    public DishView getDish(@PathVariable("id") long id) {
        LOG.debug("Getting dish id = {}", id);
        return dishService.getDish(id);
    }

    public DishInfo addTemplateDish(@RequestBody @Valid DishInfo dish) {
        LOG.debug("Adding new dish - {}", dish);
        return dishService.addTemplateDish(dish);
    }

    public void updateDish(@PathVariable("id") long id,
                           @RequestParam(required = false) Long planId,
                           @RequestBody @Valid DishInfo dish) {
        verifyEntityId(id, dish);
        LOG.debug("Updating dish {} for plan - {}", dish, planId);
        dishService.updateDish(planId, dish);
    }

    public void deleteTemplateDish(@PathVariable("id") long id) {
        LOG.debug("Deleting dish id = {}", id);
        dishService.deleteTemplateDish(id);
    }
}
