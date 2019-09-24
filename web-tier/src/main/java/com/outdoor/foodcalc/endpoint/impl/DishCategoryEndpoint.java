package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.ValidationException;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST Endpoint for Dish Category related operations
 *
 * @author Anton Borovyk.
 */
@RestController
@RequestMapping("${spring.data.rest.basePath}/dish-categories")
public class DishCategoryEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(DishCategoryEndpoint.class);

    private final List<SimpleDishCategory> categories = new ArrayList<>();

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<SimpleDishCategory> getDishCategories() {
        LOG.debug("Getting all dish categories");
        return categories;
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public SimpleDishCategory getDishCategory(@PathVariable("id") long id) {
        LOG.debug("Getting dish category id = {}", id);
        final Optional<SimpleDishCategory> first = categories.stream()
                .filter(c -> c.id == id)
                .findFirst();
        return first.orElseThrow(() -> new NotFoundException(String.valueOf(id)));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleDishCategory addDishCategory(@RequestBody @Valid SimpleDishCategory category) {
        LOG.debug("Adding new dish category - {}", category);
        category.id = categories.stream()
                .map(c -> c.id)
                .max(Long::compareTo)
                .orElse((long) categories.size())
                + 1;
        categories.add(category);
        return category;
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleDishCategory updateDishCategory(@PathVariable("id") long id,
                                   @RequestBody @Valid SimpleDishCategory category) {
        if (id != category.id) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, category.id);
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + category.id);
        }
        LOG.debug("Updating dish category {}", category);
        final Optional<SimpleDishCategory> first = categories.stream()
                .filter(c -> c.id == id)
                .findFirst();
        SimpleDishCategory original = first.orElseThrow(() -> new NotFoundException(String.valueOf(id)));
        original.name = category.name;
        return original;
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMealType(@PathVariable("id") long id) {
        LOG.debug("Deleting dish category id = {}", id);
        int index = 0;
        while (index < categories.size()) {
            if (categories.get(index).id == id) {
                categories.remove(index);
                break;
            }
            index++;
        }
    }
}
