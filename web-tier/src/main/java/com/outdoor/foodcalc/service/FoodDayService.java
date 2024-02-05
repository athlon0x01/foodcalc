package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.plan.DayPlan;
import com.outdoor.foodcalc.domain.model.plan.DayPlanRef;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import com.outdoor.foodcalc.model.plan.SimpleFoodDay;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodDayService {

    private final FoodPlansRepo repository;
    private final MealService mealService;
    private final DishService dishService;
    private final ProductService productService;

    public FoodDayService(FoodPlansRepo repository, MealService mealService, DishService dishService, ProductService productService) {
        this.repository = repository;
        this.mealService = mealService;
        this.dishService = dishService;
        this.productService = productService;
    }

    public List<FoodDayView> getAllDays(long planId) {
        return repository.getFoodPlan(planId).getDays().stream()
                .map(this::mapView)
                .collect(Collectors.toList());
    }

    public FoodDayView getDay(long planId, long id) {
        return mapView(repository.getDay(planId, id));
    }

    public void deleteFoodDay(long planId, long id) {
        var plan = repository.getFoodPlan(planId);
        var days = plan.getDays().stream()
                .filter(day -> id != day.getDayId())
                .collect(Collectors.toList());
        plan.setDays(days);
    }

    public SimpleFoodDay addFoodDay(long planId,
                                    SimpleFoodDay foodDay) {
        var plan = repository.getFoodPlan(planId);
        DayPlan dayPlan = new DayPlan(repository.getMaxDayIdAndIncrement(), foodDay.getDate(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        foodDay.setId(dayPlan.getDayId());
        var days = new ArrayList<>(plan.getDays());
        days.add(new DayPlanRef(dayPlan));
        plan.setDays(days);
        return foodDay;
    }

    public void updateFoodDay(long planId, long id,
                              SimpleFoodDay foodDay) {
        var plan = repository.getFoodPlan(planId);
        var oldDay = repository.getDay(planId, id);
        var products = foodDay.getProducts().stream()
                .map(productService::getProductRef)
                .collect(Collectors.toList());
        var updatedDishes = repository.rebuildDishes(oldDay.getDishes(), foodDay.getDishes());
        DayPlan day = new DayPlan(id, foodDay.getDate(), oldDay.getMeals(), updatedDishes, products);
        repository.updateDayInPlan(plan, day, foodDay.getDescription());
    }

    public DishView addDayDish(long planId, long dayId, long id) {
        var plan = repository.getFoodPlan(planId);
        var oldDay = repository.getDay(planId, dayId);
        //building new dish as a copy of template dish
        DishRef dishRef = dishService.getDishRefCopy(id, repository.getMaxDishIdAndIncrement());
        //TODO new dish should be persisted and linked to the day
        List<DishRef> dishes = new ArrayList<>(oldDay.getDishes());
        dishes.add(dishRef);
        DayPlan day = new DayPlan(dayId, oldDay.getDate(), oldDay.getMeals(), dishes, oldDay.getProducts());
        repository.updateDayInPlan(plan, day, oldDay.getDescription());
        return dishService.mapDishView(dishRef);
    }

    FoodDayView mapView(DayPlanRef day) {
        return FoodDayView.builder()
                .id(day.getDayId())
                .date(day.getDate())
                .description(day.getDescription())
                .meals(day.getMeals().stream()
                        .map(mealService::mapView)
                        .collect(Collectors.toList()))
                .products(day.getProducts().stream()
                        .map(productService::mapProductRef)
                        .collect(Collectors.toList()))
                .dishes(day.getDishes().stream()
                        .map(dishService::mapDishView)
                        .collect(Collectors.toList()))
                .calorific(day.getCalorific())
                .carbs(day.getCarbs())
                .fats(day.getFats())
                .proteins(day.getProteins())
                .weight(day.getWeight())
                .build();
    }
}
