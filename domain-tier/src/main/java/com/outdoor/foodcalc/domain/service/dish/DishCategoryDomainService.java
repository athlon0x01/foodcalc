package com.outdoor.foodcalc.domain.service.dish;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;

import com.outdoor.foodcalc.domain.repository.dish.IDishCategoryRepo;
import com.outdoor.foodcalc.domain.repository.dish.IDishRepo;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Domain service for all operations with {@link DishCategory} objects.
 *
 * @author Olga Borovyk.
 */
@Service
public class DishCategoryDomainService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCategoryDomainService.class);

    private final IDishCategoryRepo categoryRepo;

    private final IDishRepo dishRepo;

    @Autowired
    public DishCategoryDomainService(IDishCategoryRepo categoryRepo, IDishRepo dishRepo) {
        this.categoryRepo = categoryRepo;
        this.dishRepo = dishRepo;
    }

    /**
     * Loads all {@link DishCategory} objects.
     *
     * @return list of categories
     */
    public List<DishCategory> getCategories() {
        return categoryRepo.getCategories();
    }

    /**
     * Loads {@link DishCategory} object by Id.
     *
     * @param id category Id to load
     * @return loaded category
     */
    public Optional<DishCategory> getCategory(long id) {
        return categoryRepo.getCategory(id);
    }

    /**
     * Add new {@link DishCategory}.
     *
     * @param category category to add
     * @return instance of new {@link DishCategory}
     */
    public DishCategory addCategory(DishCategory category) {
        long id = categoryRepo.addCategory(category);
        return new DishCategory(id, category.getName());
    }

    /**
     * Updates selected {@link DishCategory} with new value.
     *
     * @param category updated category
     * @throws NotFoundException If dish category doesn't exist
     * @throws FoodcalcDomainException If dish category wasn't updated
     */
    public void updateCategory(DishCategory category) {
        if (!categoryRepo.exist(category.getCategoryId())) {
            LOG.error("Dish category with id={} doesn't exist", category.getCategoryId());
            throw new NotFoundException("Dish category with id=" + category.getCategoryId() + " doesn't exist");
        }
        if(!categoryRepo.updateCategory(category)) {
            LOG.error("Failed to update dish category with id=" + category.getCategoryId());
            throw new FoodcalcDomainException("Failed to update dish category with id=" + category.getCategoryId());
        }
    }

    /**
     * Removes selected {@link DishCategory}.
     *
     * @param id category Id to delete
     * @throws NotFoundException If dish category doesn't exist
     * @throws FoodcalcDomainException If dish category wasn't deleted
     */
    public void deleteCategory(long id) {
        if (!categoryRepo.exist(id)) {
            LOG.error("Dish category with id={} doesn't exist", id);
            throw new NotFoundException("Dish category with id=" + id + " doesn't exist");
        }
        if (dishRepo.countDishesInCategory(id) == 0) {
            if (!categoryRepo.deleteCategory(id)) {
                LOG.error("Failed to delete dish category with id=" + id);
                throw new FoodcalcDomainException("Failed to delete dish category with id=" + id);
            }
        } else {
            LOG.error("Dish category with id = {} is not empty", id);
            throw new IllegalArgumentException("Dish category with id=" + id + " is not empty");
            }
    }
}
