package com.outdoor.foodcalc.domain.service.meal;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.repository.meal.IMealRepo;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRefRepo;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MealDomainService {

    private final IFoodPlanRepo planRepo;
    private final IMealRepo mealRepo;
    private final IProductRefRepo productRefRepo;
    private final MealTypeDomainService mealTypeDomainService;
    private final DishDomainService dishService;

    public MealDomainService(IFoodPlanRepo planRepo, IMealRepo mealRepo, IProductRefRepo productRefRepo, MealTypeDomainService mealTypeDomainService, DishDomainService dishService) {
        this.planRepo = planRepo;
        this.mealRepo = mealRepo;
        this.productRefRepo = productRefRepo;
        this.mealTypeDomainService = mealTypeDomainService;
        this.dishService = dishService;
    }

    public Map<Long, List<Meal>> getMealsForAllDaysInPlan(long planId) {
        var meals = mealRepo.getAllMealsInPlan(planId);
        //load dishes for all meals
        var planMealsDishes = dishService.getMealDishesForAllDaysInPlan(planId);
        //load products for all meals and set them
        var planMealsProducts = productRefRepo.getMealProductsForAllMealsInPlan(planId);
        meals.values().stream()
                .flatMap(List::stream)
                .forEach(meal -> {
                    Optional.ofNullable(planMealsDishes.get(meal.getMealId()))
                            .ifPresent(meal::setDishes);
                    Optional.ofNullable(planMealsProducts.get(meal.getMealId()))
                            .ifPresent(meal::setProducts);
                });
        return meals;
    }

    public List<Meal> getDayMeals(long dayId) {
        var meals = mealRepo.getDayMeals(dayId);
        //load dishes for all days
        var dayMealsDishes = dishService.getMealDishesForAllMealsInDay(dayId);
        //load products for all days and set them
        var dayMealsProducts = productRefRepo.getMealProductsForAllMealsInDay(dayId);
        meals.forEach(meal -> {
            Optional.ofNullable(dayMealsDishes.get(meal.getMealId()))
                    .ifPresent(meal::setDishes);
            Optional.ofNullable(dayMealsProducts.get(meal.getMealId()))
                    .ifPresent(meal::setProducts);
        });
        return meals;
    }

    public Optional<Meal> getMeal(long id) {
        return mealRepo.getMeal(id).map(meal -> {
            meal.setDishes(dishService.getMealDishes(id));
            meal.setProducts(productRefRepo.getMealProducts(id));
            return meal;
        });
    }

    public void deleteMeal(long planId, long id) {
        if(!mealRepo.existsMeal(id)) {
            throw new NotFoundException("Meal with id=" + id + " doesn't exist");
        }
        productRefRepo.deleteMealProducts(id);
        dishService.deleteAllDishesForMeal(id);
        mealRepo.detachMeal(id);
        mealRepo.deleteMeal(id);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }

    public Meal addMeal(long planId, long dayId, Meal meal) {
        var mealType = mealTypeDomainService.getMealType(meal.getType().getTypeId())
                .orElseThrow(() -> new NotFoundException("Failed to get Meal Type, id = " + meal.getType().getTypeId()));
        long id = mealRepo.addMeal(meal);
        mealRepo.attachMeal(dayId, id);
        Meal newMeal = Meal.builder()
                .mealId(id)
                .type(mealType)
                .build();
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        return newMeal;
    }

    public void updateMeal(long planId, Meal meal) {
        long mealId = meal.getMealId();
        if(!mealRepo.existsMeal(mealId)) {
            throw new NotFoundException("Meal with id=" + mealId + " doesn't exist");
        }
        var mealType = mealTypeDomainService.getMealType(meal.getType().getTypeId())
                .orElseThrow(() -> new NotFoundException("Failed to get Meal Type, id = " + meal.getType().getTypeId()));
        meal.setType(mealType);
        //updating products of the day
        productRefRepo.deleteMealProducts(mealId);
        if (!productRefRepo.insertMealProducts(meal)) {
            throw new FoodcalcDomainException("Failed to add products for meal with id=" + mealId);
        }
        //TODO update dishes order
        //dishService.updateMealDishes(mealId, meal.getDishes());
        if(!mealRepo.updateMeal(meal)) {
            throw new FoodcalcDomainException("Failed to update meal with id=" + mealId);
        }
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }
}
