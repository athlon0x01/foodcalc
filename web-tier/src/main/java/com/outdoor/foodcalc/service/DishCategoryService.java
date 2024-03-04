package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.model.dish.DishCategoryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional(readOnly = true)
public class DishCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(DishCategoryService.class);

    private final DishCategoryDomainService categoryDomainService;

    @Autowired
    public DishCategoryService(DishCategoryDomainService categoryDomainService) {
        this.categoryDomainService = categoryDomainService;
    }

    /**
     * Gets all {@link DishCategory} objects mapped to {@link DishCategoryView}.
     *
     * @return list of categories
     */
    public List<DishCategoryView> getDishCategories() {
        return categoryDomainService.getCategories().stream()
                .map(this::mapDishCategory)
                .collect(Collectors.toList());
    }

    private DishCategoryView mapDishCategory(DishCategory category) {
        return DishCategoryView.builder()
                .id(category.getCategoryId())
                .name(category.getName())
                .build();
    }

    /**
     * Gets all {@link DishCategory} objects mapped to {@link DishCategoryView}.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public DishCategoryView getDishCategory(long id) {
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public DishCategoryView addDishCategory(String categoryName) {
        return mapDishCategory(
                categoryDomainService.addCategory(
                        new DishCategory(-1, categoryName)));
    }

    /**
     * Updates selected {@link DishCategory} with new value.
     *
     * @param model updated category
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateDishCategory(DishCategoryView model) {
        categoryDomainService.updateCategory(new DishCategory(model.getId(), model.getName()));
    }

    /**
     * Removes selected {@link DishCategory}.
     *
     * @param id category Id to delete
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteDishCategory(long id) {
        categoryDomainService.deleteCategory(id);
    }

}
