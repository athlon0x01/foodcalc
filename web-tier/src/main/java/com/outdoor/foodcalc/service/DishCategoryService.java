package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.model.dish.DishCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link com.outdoor.foodcalc.domain.model.dish.DishCategory} objects,
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
     * Gets all {@link com.outdoor.foodcalc.domain.model.dish.DishCategory} objects mapped to {@link DishCategory}.
     *
     * @return list of categories
     */
    public List<DishCategory> getDishCategories() {
        return categoryDomainService.getCategories().stream()
                .map(this::mapDishCategory)
                .collect(Collectors.toList());
    }

    private DishCategory mapDishCategory(com.outdoor.foodcalc.domain.model.dish.DishCategory category) {
        return DishCategory.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                .build();
    }

    /**
     * Gets all {@link com.outdoor.foodcalc.domain.model.dish.DishCategory} objects mapped to {@link DishCategory}.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public DishCategory getDishCategory(long id) {
        Optional<com.outdoor.foodcalc.domain.model.dish.DishCategory> category = categoryDomainService.getCategory(id);
        if (!category.isPresent()) {
            LOG.error("Dish category with id={} wasn't found", id);
            throw new NotFoundException("Dish category wasn't found");
        }
        return mapDishCategory(category.get());
    }

    /**
     * Add new {@link com.outdoor.foodcalc.domain.model.dish.DishCategory}.
     *
     * @param categoryName name of new category
     * @return new category
     */
    public DishCategory addDishCategory(String categoryName) {
        return mapDishCategory(
                categoryDomainService.addCategory(
                        new com.outdoor.foodcalc.domain.model.dish.DishCategory(-1, categoryName)));
    }

    /**
     * Updates selected {@link com.outdoor.foodcalc.domain.model.dish.DishCategory} with new value.
     *
     * @param model updated category
     */
    public void updateDishCategory(DishCategory model) {
        categoryDomainService.updateCategory(new com.outdoor.foodcalc.domain.model.dish.DishCategory(model.getId(), model.getName()));
    }

    /**
     * Removes selected {@link com.outdoor.foodcalc.domain.model.dish.DishCategory}.
     *
     * @param id category Id to delete
     */
    public void deleteDishCategory(long id) {
        categoryDomainService.deleteCategory(id);
    }

}
