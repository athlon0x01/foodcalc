package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.meal.MealInfo;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.model.meal.MealView;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final FoodPlansRepo repository;
    private final MealTypeDomainService mealTypeService;
    private final DishService dishService;
    private final ProductService productService;

    public MealService(FoodPlansRepo repository, MealTypeDomainService mealTypeService, DishService dishService, ProductService productService) {
        this.repository = repository;
        this.mealTypeService = mealTypeService;
        this.dishService = dishService;
        this.productService = productService;
    }

    public List<MealView> getAllMeals(long planId, long dayId) {
        return repository.getDay(planId, dayId).getMeals().stream()
                .map(this::mapView)
                .collect(Collectors.toList());
    }

    public MealView getMeal(long planId, long dayId, long id) {
        return mapView(repository.getMeal(planId, dayId, id));
    }

    public void deleteMeal(long planId, long dayId, long id) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        var meals = day.getMeals().stream()
                .filter(meal -> id != meal.getMealId())
                .collect(Collectors.toList());
        day.setMeals(meals);
        plan.setLastUpdated(ZonedDateTime.now());
    }

    public MealInfo addMeal(long planId, long dayId, MealInfo meal) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        Meal newMeal = Meal.builder()
                .mealId(repository.getMaxMealIdAndIncrement())
                .type(getMealType(meal.getTypeId()))
                .build();
        meal.setId(newMeal.getMealId());
        day.getMeals().add(newMeal);
        plan.setLastUpdated(ZonedDateTime.now());
        return meal;
    }

    public void updateMeal(long planId, long dayId, long id, MealInfo newMeal) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        var meal = repository.getMeal(planId, dayId, id);
        var products = newMeal.getProducts().stream()
                .map(productService::getProductRef)
                .collect(Collectors.toList());
        var updatedDishes = repository.reorderDishes(meal.getDishes(), newMeal.getDishes());
        Meal domainMeal = meal.toBuilder()
                .mealId(id)
                .type(getMealType(newMeal.getTypeId()))
                .dishes(updatedDishes)
                .products(products)
                .build();
        repository.updateMealInDay(plan, day, domainMeal, newMeal.getDescription());
    }

    public DishView addMealDish(long planId, long dayId, long mealId, long id) {
        var plan = repository.getFoodPlan(planId);
        var meal = repository.getMeal(planId, dayId, mealId);
        //building new dish as a copy of template dish
        Dish dish = dishService.getDishCopy(id, repository.getMaxDishIdAndIncrement());
        //TODO new dish should be persisted and linked to the meal
        meal.getDishes().add(dish);
        plan.setLastUpdated(ZonedDateTime.now());
        return dishService.mapDishView(dish);
    }

    private MealType getMealType(long id) {
        var mealTypes = mealTypeService.getMealTypes();
        return mealTypes.stream()
                .filter(type -> id == type.getTypeId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MealType with id = " + id + " wasn't found"));
    }

    MealView mapView(Meal meal) {
        FoodDetailsInstance mealDetails = meal.getFoodDetails();
        return MealView.builder()
                .id(meal.getMealId())
                .description(meal.getDescription())
                .type(MealTypeView.builder()
                        .id(meal.getType().getTypeId())
                        .name(meal.getType().getName())
                        .build())
                .products(meal.getProducts().stream()
                        .map(productService::mapProductRef)
                        .collect(Collectors.toList()))
                .dishes(meal.getDishes().stream()
                        .map(dishService::mapDishView)
                        .collect(Collectors.toList()))
                .calorific(mealDetails.getCalorific())
                .carbs(mealDetails.getCarbs())
                .fats(mealDetails.getFats())
                .proteins(mealDetails.getProteins())
                .weight(mealDetails.getWeight())
                .build();
    }
}
