package com.outdoor.foodcalc.domain.service.dish;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.dish.IDishRepo;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRefRepo;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Domain service for all operations with {@link Dish} objects.
 *
 * @author Olga Borovyk
 */
@Service
public class DishDomainService {

    private final IFoodPlanRepo planRepo;
    private final IDishRepo dishRepo;
    private final IProductRefRepo productRefRepo;
    private final DishCategoryDomainService dishCategoryService;

    public DishDomainService(IFoodPlanRepo planRepo, IDishRepo dishRepo, IProductRefRepo productRefRepo, DishCategoryDomainService dishCategoryService) {
        this.planRepo = planRepo;
        this.dishRepo = dishRepo;
        this.productRefRepo = productRefRepo;
        this.dishCategoryService = dishCategoryService;
    }

    /**
     * Loads all {@link Dish} objects.
     *
     * @return list of dishes
     */
    public List<Dish> getAllTemplateDishes() {
        List<Dish> dishes = dishRepo.getAllTemplateDishes();
        Map<Long, List<ProductRef>> allDishIdWithProductRefs = productRefRepo.getAllTemplateDishesProducts();

        dishes.stream()
                .filter(dish -> allDishIdWithProductRefs.containsKey(dish.getDishId()))
                .forEach(dish -> dish.setProducts(allDishIdWithProductRefs.get(dish.getDishId())));
        return dishes;
    }

    public List<Dish> getMealDishes(long mealId) {
        var dishesProducts = productRefRepo.getMealDishesProducts(mealId);
        var dishes = dishRepo.getMealDishes(mealId);
        dishes.forEach(
                dish -> Optional.ofNullable(dishesProducts.get(dish.getDishId()))
                        .ifPresent(dish::setProducts)
        );
        return dishes;
    }

    public List<Dish> getDayDishes(long dayId) {
        var dishesProducts = productRefRepo.getDayDishesProducts(dayId);
        var dishes = dishRepo.getDayDishes(dayId);
        dishes.forEach(
                dish -> Optional.ofNullable(dishesProducts.get(dish.getDishId()))
                        .ifPresent(dish::setProducts)
        );
        return dishes;
    }

    public Map<Long, List<Dish>> getDayDishesForAllDaysInPlan(long planId) {
        var dishesProducts = productRefRepo.getDishesProductsForAllDaysInPlan(planId);
        var dishes = dishRepo.getDayDishesForAllDaysInPlan(planId);
        dishes.values().stream()
                .flatMap(List::stream).forEach(
                        dish -> Optional.ofNullable(dishesProducts.get(dish.getDishId()))
                                .ifPresent(dish::setProducts)
                );
        return dishes;
    }

    public Map<Long, List<Dish>> getMealDishesForAllMealsInDay(long dayId) {
        var dishesProducts = productRefRepo.getDishesProductsForAllMealsInDay(dayId);
        var dishes = dishRepo.getMealDishesForAllMealsInDay(dayId);
        dishes.values().stream()
                .flatMap(List::stream).forEach(
                        dish -> Optional.ofNullable(dishesProducts.get(dish.getDishId()))
                                .ifPresent(dish::setProducts)
                );
        return dishes;
    }

    public Map<Long, List<Dish>> getMealDishesForAllDaysInPlan(long planId) {
        var dishesProducts = productRefRepo.getDishesProductsForAllMealsInPlan(planId);
        var dishes = dishRepo.getMealDishesForAllMealsInPlan(planId);
        dishes.values().stream()
                .flatMap(List::stream).forEach(
                        dish -> Optional.ofNullable(dishesProducts.get(dish.getDishId()))
                                .ifPresent(dish::setProducts)
                );
        return dishes;
    }

    /**
     * Loads {@link Dish} object.
     *
     * @param id dish id
     * @return loaded dish
     */
    public Optional<Dish> getDish(long id) {
        Optional<Dish> dish = dishRepo.getDish(id);
        dish.ifPresent(value -> {
            List<ProductRef> dishProducts = productRefRepo.getDishProducts(value.getDishId());
            if(!dishProducts.isEmpty()) {
                value.setProducts(dishProducts);
            }
        });
        return dish;
    }

    /**
     * Add new {@link Dish} object.
     *
     * @param dish to add
     *
     * @throws FoodcalcDomainException If dish wasn't added
     * @throws FoodcalcDomainException If dish products weren't added
     * @return new {@link Dish} with auto generated id
     */
    public Dish addTemplateDish(Dish dish) {
        final DishCategory category = dishCategoryService.getCategory(dish.getCategory().getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Dish Category, id = " + dish.getCategory().getCategoryId() ));
        dish.setCategory(category);
        return insertDishWithProducts(dish);
    }

    private Dish insertDishWithProducts(Dish dish) {
        long id = dishRepo.addDish(dish);
        if(id == -1L) {
            throw new FoodcalcDomainException("Failed to add dish");
        }
        Dish addedDish = dish.toBuilder()
                .dishId(id)
                .build();
        if(!productRefRepo.insertDishProducts(addedDish)) {
            throw new FoodcalcDomainException("Fail to add products for dish with id=" + addedDish.getDishId());
        }
        return addedDish;
    }

    public Dish addMealDish(long planId, long mealId, long templateId) {
        //building new dish as a copy of template dish
        Optional<Dish> mealDish = buildNonTemplateDish(templateId);
        if (mealDish.isPresent()) {
            Dish newDish = insertDishWithProducts(mealDish.get());
            dishRepo.attachDishToMeal(mealId, newDish.getDishId());
            planRepo.saveLastUpdated(planId, ZonedDateTime.now());
            return newDish;
        } else {
            throw new FoodcalcDomainException("Fail to load template dish with id=" + templateId);
        }
    }

    public Dish addDayDish(long planId, long dayId, long templateId) {
        //building new dish as a copy of template dish
        Optional<Dish> dayDish = buildNonTemplateDish(templateId);
        if (dayDish.isPresent()) {
            Dish newDish = insertDishWithProducts(dayDish.get());
            dishRepo.attachDishToDay(dayId, newDish.getDishId());
            planRepo.saveLastUpdated(planId, ZonedDateTime.now());
            return newDish;
        } else {
            throw new FoodcalcDomainException("Fail to load template dish with id=" + templateId);
        }
    }

    public void deleteMealDish(long planId, long mealId, long dishId) {
        if (dishRepo.detachDishFromMeal(mealId, dishId)) {
            deleteTemplateDish(dishId);
            planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        } else {
            throw new FoodcalcDomainException("Fail to detach dish with id=" + dishId + " from meal id=" + mealId);
        }
    }

    public void deleteDayDish(long planId, long dayId, long dishId) {
        if (dishRepo.detachDishFromDay(dayId, dishId)) {
            deleteTemplateDish(dishId);
            planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        } else {
            throw new FoodcalcDomainException("Fail to detach dish with id=" + dishId + " from day id=" + dayId);
        }
    }

    /**
     * Update {@link Dish} object.
     *
     * @param dish to update
     *
     * @throws NotFoundException If dish doesn't exist
     * @throws FoodcalcDomainException If dish products weren't added
     * @throws FoodcalcDomainException If dish wasn't updated
     */
    public void updateDish(Long planId, Dish dish) {
        if (!dishRepo.existsDish(dish.getDishId())) {
            throw new NotFoundException("Dish with id=" + dish.getDishId() + " doesn't exist");
        }
        final DishCategory category = dishCategoryService.getCategory(dish.getCategory().getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Dish Category, id = " + dish.getCategory().getCategoryId()));
        dish.setCategory(category);
        productRefRepo.deleteDishProducts(dish.getDishId());
        if (!productRefRepo.insertDishProducts(dish)) {
            throw new FoodcalcDomainException("Failed to add products for dish with id=" + dish.getDishId());
        }
        if (!dishRepo.updateDish(dish)) {
            throw new FoodcalcDomainException("Failed to update dish with id=" + dish.getDishId());
        }
        if (planId != null) {
            planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        }
    }

    /**
     * Delete {@link Dish} object with all related {@link ProductRef}.
     *
     * @param id dish to delete
     *
     * @throws NotFoundException If dish doesn't exist
     * @throws FoodcalcDomainException If dish wasn't deleted
     */
    public void deleteTemplateDish(long id) {
        if(!dishRepo.existsDish(id)) {
            throw new NotFoundException("Dish with id=" + id + " doesn't exist");
        }
        productRefRepo.deleteDishProducts(id);
        if(!dishRepo.deleteDish(id)) {
            throw new FoodcalcDomainException("Failed to delete dish with id=" + id);
        }
    }

    public void deleteAllDishesForMeal(long mealId) {
        productRefRepo.deleteAllDishProductsForMeal(mealId);
        dishRepo.detachAllDishesFromMeal(mealId);
        dishRepo.deleteAllDishesForMeal(mealId);
    }

    public void deleteAllDishesForDay(long dayId) {
        productRefRepo.deleteAllDishProductsForDay(dayId);
        dishRepo.detachAllDishesFromDay(dayId);
        dishRepo.deleteAllDishesForDay(dayId);
    }

    private Optional<Dish> buildNonTemplateDish(long id) {
        Optional<Dish> templateDish = getDish(id);
        return templateDish.map(dish -> dish.toBuilder()
                .dishId(-1)
                .template(false)
                .build());
    }
}
