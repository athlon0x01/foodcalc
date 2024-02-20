package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import com.outdoor.foodcalc.model.plan.FoodDayInfo;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        plan.setLastUpdated(ZonedDateTime.now());
    }

    public FoodDayInfo addFoodDay(long planId,
                                  FoodDayInfo foodDay) {
        var plan = repository.getFoodPlan(planId);
        PlanDay planDay = PlanDay.builder()
                .dayId(repository.getMaxDayIdAndIncrement())
                .date(foodDay.getDate())
                .build();
        foodDay.setId(planDay.getDayId());
        plan.getDays().add(planDay);
        plan.setLastUpdated(ZonedDateTime.now());
        return foodDay;
    }

    public void updateFoodDay(long planId, long id,
                              FoodDayInfo foodDay) {
        var plan = repository.getFoodPlan(planId);
        var oldDay = repository.getDay(planId, id);
        var products = foodDay.getProducts().stream()
                .map(productService::getProductRef)
                .collect(Collectors.toList());
        var updatedMeals = reorderMeals(oldDay.getMeals(), foodDay.getMeals());
        var updatedDishes = repository.reorderDishes(oldDay.getDishes(), foodDay.getDishes());
        PlanDay day = oldDay.toBuilder()
                .dayId(id)
                .date(foodDay.getDate())
                .meals(updatedMeals)
                .dishes(updatedDishes)
                .products(products)
                .build();
        repository.updateDayInPlan(plan, day, foodDay.getDescription());
    }

    public DishView addDayDish(long planId, long dayId, long id) {
        var plan = repository.getFoodPlan(planId);
        var day = repository.getDay(planId, dayId);
        //building new dish as a copy of template dish
        Dish dish = dishService.getDishCopy(id, repository.getMaxDishIdAndIncrement());
        //TODO new dish should be persisted and linked to the day
        day.getDishes().add(dish);
        plan.setLastUpdated(ZonedDateTime.now());
        return dishService.mapDishView(dish);
    }

    public Optional<Meal> getMealById(List<Meal> meals, long id) {
        return meals.stream()
                .filter(dish -> dish.getMealId() == id)
                .findFirst();
    }

    public List<Meal> reorderMeals(List<Meal> meals, List<Long> ids) {
        List<Meal> newMeals = new ArrayList<>();
        ids.forEach(id -> getMealById(meals, id)
                .ifPresent(newMeals::add));
        return newMeals;
    }

    FoodDayView mapView(PlanDay day) {
        FoodDetailsInstance dayDetails = day.getFoodDetails();
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
                .calorific(dayDetails.getCalorific())
                .carbs(dayDetails.getCarbs())
                .fats(dayDetails.getFats())
                .proteins(dayDetails.getProteins())
                .weight(dayDetails.getWeight())
                .build();
    }
}
