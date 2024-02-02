package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealRef;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.model.plan.DayPlan;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.dish.SimpleDish;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.model.meal.MealView;
import com.outdoor.foodcalc.model.meal.SimpleMeal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
        DayPlan newDay = new DayPlan(id, day.getDate(), meals, day.getDishes(), day.getProducts());
        repository.updateDayInPlan(plan, newDay, day.getDescription());
    }

    public SimpleMeal addMeal(long planId, long dayId, SimpleMeal meal) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        Meal newMeal = new Meal(repository.getMaxMealIdAndIncrement(), getMealType(meal.getTypeId()), Collections.emptyList(), Collections.emptyList());
        var meals = day.getMeals();
        meals.add(new MealRef(newMeal));
        DayPlan newDay = new DayPlan(dayId, day.getDate(), meals, day.getDishes(), day.getProducts());
        repository.updateDayInPlan(plan, newDay, day.getDescription());
        return meal;
    }

    public void updateMeal(long planId, long dayId, long id, SimpleMeal newMeal) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        var meal = repository.getMeal(planId, dayId, id);
        var products = newMeal.getProducts().stream()
                .map(productService::getProductRef)
                .collect(Collectors.toList());
        //TODO update dish order
        Meal domainMeal = new Meal(id, getMealType(newMeal.getTypeId()), meal.getDishes(), products);
        repository.updateMealInDay(plan, day, domainMeal, meal.getDescription());
    }

    public void updateMealDish(long planId, long dayId, long mealId, SimpleDish newDish) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        var meal = repository.getMeal(planId, dayId, mealId);
        DishRef dishRef = dishService.mapDishRef(newDish);
        List<DishRef> dishes = new ArrayList<>(meal.getDishes());
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getDishId() == dishRef.getDishId()) {
                dishes.set(i, dishRef);
            }
        }
        Meal domainMeal = new Meal(mealId, getMealType(meal.getTypeId()), dishes, meal.getProducts());
        repository.updateMealInDay(plan, day, domainMeal, meal.getDescription());
    }

    public DishView addMealDish(long planId, long dayId, long mealId, long id) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        var meal = repository.getMeal(planId, dayId, mealId);
        //building new dish as a copy of template dish
        DishRef dishRef = dishService.getDishRefCopy(id, repository.getMaxDishIdAndIncrement());
        //TODO new dish should be persisted and linked to the meal
        List<DishRef> dishes = new ArrayList<>(meal.getDishes());
        dishes.add(dishRef);
        Meal domainMeal = new Meal(mealId, getMealType(meal.getTypeId()), dishes, meal.getProducts());
        repository.updateMealInDay(plan, day, domainMeal, meal.getDescription());
        return dishService.mapDishView(dishRef);
    }

    private MealType getMealType(long id) {
        var mealTypes = mealTypeService.getMealTypes();
        return mealTypes.stream()
                .filter(type -> id == type.getTypeId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MealType with id = " + id + " wasn't found"));
    }

    public MealView mapView(MealRef meal) {
        return MealView.builder()
                .id(meal.getMealId())
                .description(meal.getDescription())
                .type(new MealTypeView(meal.getTypeId(), meal.getTypeName()))
                .products(meal.getProducts().stream()
                        .map(productService::mapProductRef)
                        .collect(Collectors.toList()))
                .dishes(meal.getDishes().stream()
                        .map(dishService::mapDishView)
                        .collect(Collectors.toList()))
                .calorific(meal.getCalorific())
                .carbs(meal.getCarbs())
                .fats(meal.getFats())
                .proteins(meal.getProteins())
                .weight(meal.getWeight())
                .build();
    }
}
