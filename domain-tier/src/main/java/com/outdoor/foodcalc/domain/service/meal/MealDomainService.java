package com.outdoor.foodcalc.domain.service.meal;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.repository.meal.IMealRepo;
import com.outdoor.foodcalc.domain.service.FoodPlansRepo;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealDomainService {

    private final IMealRepo mealRepo;
    private final MealTypeDomainService mealTypeDomainService;
    private final ProductDomainService productService;
    private final DishDomainService dishService;
    private final FoodPlansRepo tmpRepo;

    public MealDomainService(IMealRepo mealRepo, MealTypeDomainService mealTypeDomainService, ProductDomainService productService, DishDomainService dishService, FoodPlansRepo tmpRepo) {
        this.mealRepo = mealRepo;
        this.mealTypeDomainService = mealTypeDomainService;
        this.productService = productService;
        this.dishService = dishService;
        this.tmpRepo = tmpRepo;
    }

    public List<Meal> getAllMeals(long planId, long dayId) {
        return tmpRepo.getDay(planId, dayId).getMeals();
    }

    public Optional<Meal> getMeal(long planId, long dayId, long id) {
        return Optional.ofNullable(tmpRepo.getMeal(planId, dayId, id));
    }

    public void deleteMeal(long planId, long dayId, long id) {
        var plan = tmpRepo.getFoodPlan(planId);
        var day = tmpRepo.getDay(planId, dayId);
        var meals = day.getMeals().stream()
                .filter(meal -> id != meal.getMealId())
                .collect(Collectors.toList());
        day.setMeals(meals);
        plan.setLastUpdated(ZonedDateTime.now());
    }

    public Meal addMeal(long planId, long dayId, Meal meal) {
        var mealType = mealTypeDomainService.getMealType(meal.getType().getTypeId())
                .orElseThrow(() -> new NotFoundException("Failed to get Meal Type, id = " + meal.getType().getTypeId()));
        var plan = tmpRepo.getFoodPlan(planId);
        var day = tmpRepo.getDay(planId, dayId);
        Meal newMeal = Meal.builder()
                .mealId(tmpRepo.getMaxMealIdAndIncrement())
                .type(mealType)
                .build();
        day.getMeals().add(newMeal);
        plan.setLastUpdated(ZonedDateTime.now());
        return newMeal;
    }

    public void updateMeal(long planId, long dayId, Meal meal) {
        var mealType = mealTypeDomainService.getMealType(meal.getType().getTypeId())
                .orElseThrow(() -> new NotFoundException("Failed to get Meal Type, id = " + meal.getType().getTypeId()));
        var plan = tmpRepo.getFoodPlan(planId);
        var day = tmpRepo.getDay(planId, dayId);
        var oldMeal = tmpRepo.getMeal(planId, dayId, meal.getMealId());
        meal.setProducts(meal.getProducts().stream()
                .map(productService::loadProduct)
                .collect(Collectors.toList()));
        meal.setDishes(tmpRepo.reorderDishes(oldMeal.getDishes(), meal.getDishes()));
        meal.setType(mealType);
        tmpRepo.updateMealInDay(plan, day, meal, meal.getDescription());
    }

    public Dish addMealDish(long planId, long dayId, long mealId, long id) {
        var plan = tmpRepo.getFoodPlan(planId);
        var meal = tmpRepo.getMeal(planId, dayId, mealId);
        //building new dish as a copy of template dish
        Dish dish = dishService.getDishCopy(id, tmpRepo.getMaxDishIdAndIncrement());
        //TODO new dish should be persisted and linked to the meal
        meal.getDishes().add(dish);
        plan.setLastUpdated(ZonedDateTime.now());
        return dish;
    }
}
