package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link DishCategory} objects,
 * including transformation domain objects to view models and vise versa.
 *
 * @author Olga Borovyk.
 */
@Service
public class DishCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(DishCategoryService.class);

    private final DishCategoryDomainService categoryDomainService;

    @Autowired
    public DishCategoryService(DishCategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    /**
     * Gets all {@link DishCategory} objects mapped to {@link SimpleDishCategory}.
     *
     * @return list of categories
     */
    public List<SimpleDishCategory> getDishCategories() {
        return categoryDomainService.getCategories().stream()
                .map(this::mapDishCategory)
                .collect(Collectors.toList());
    }

    private SimpleDishCategory mapDishCategory(DishCategory category) {
        final SimpleDishCategory model = new SimpleDishCategory(category.getCategoryId(), category.getName());
        return model;
    }

    /**
     * Gets all {@link DishCategory} objects mapped to {@link SimpleDishCategory}.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public SimpleDishCategory getDishCategory(long id) {
        Optional<DishCategory> category = categoryDomainService.getCategory(id);
        if (!category.isPresent()) {
            LOG.error("Dish category with id={} wasn't found", id);
            throw new NotFoundException("Dish category wasn't found");
        }
        return mapDishCategory(category.get());
    }

    /**
     * Add new {@link DishCategory}.
     *
     * @param categoryName name of new category
     * @return new category
     */
    public SimpleDishCategory addDishCategory(String categoryName) {
        return mapDishCategory(
                categoryDomainService.addCategory(
                        new DishCategory(-1, categoryName)));
    }

    /**
     * Updates selected {@link DishCategory} with new value.
     *
     * @param model updated category
     */
    public boolean updateDishCategory(SimpleDishCategory model) {
        return categoryDomainService.updateCategory(new DishCategory(model.getId(), model.getName()));
    }

    /**
     * Removes selected {@link DishCategory}.
     *
     * @param id category Id to delete
     */
    public boolean deleteDishCategory(long id) {
        return categoryDomainService.deleteCategory(id);
    }

}
