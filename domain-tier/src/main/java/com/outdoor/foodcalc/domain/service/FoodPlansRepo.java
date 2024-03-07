package com.outdoor.foodcalc.domain.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.DishesContainer;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import org.springframework.stereotype.Component;

import java.util.*;

//TODO temporary holder \ repository for food days \ meals \ dishes
@Deprecated(forRemoval = true)
@Component
public class FoodPlansRepo {

    private final Map<Long, List<PlanDay>> foodPlanDays = new HashMap<>();
    private long maxDayId = 104L;
    private long maxMealId = 10107L;
    private long maxDishId = 1010101L;

    public long getMaxDayIdAndIncrement() {
        return maxDayId++;
    }

    public long getMaxMealIdAndIncrement() {
        return maxMealId++;
    }

    public long getMaxDishIdAndIncrement() {
        return maxDishId++;
    }

    public List<PlanDay> getPlanDays(long planId) {
        List<PlanDay> planDays = foodPlanDays.get(planId);
        return planDays == null ? new ArrayList<>() : planDays;
    }

    public void deleteFoodPlan(long id) {
        foodPlanDays.remove(id);
    }

    public void addFoodPlan(long planId) {
        foodPlanDays.put(planId, new ArrayList<>());
    }

    public PlanDay getDay(long planId, long id) {
        var plan = getPlanDays(planId);
        return plan.stream()
                .filter(day -> id == day.getDayId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Food plan day with id = " + id + " wasn't found"));
    }

    public void deletePlanDay(long planId, long id) {
        List<PlanDay> planDays = getPlanDays(planId);
        planDays.stream()
                .filter(day -> id == day.getDayId())
                .forEach(planDays::remove);
    }

    public Meal getMeal(long planId, long dayId, long id) {
        var day = getDay(planId, dayId);
        return day.getMeals().stream()
                .filter(meal -> id == meal.getMealId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Meal with id = " + id + " wasn't found"));
    }

    public Optional<Dish> getDishById(List<Dish> dishes, long id) {
        return dishes.stream()
                .filter(dish -> dish.getDishId() == id)
                .findFirst();
    }

    public List<Dish> reorderDishes(List<Dish> dishes, List<Dish> ids) {
        List<Dish> newDishes = new ArrayList<>();
        ids.forEach(id -> getDishById(dishes, id.getDishId())
                .ifPresent(newDishes::add));
        return newDishes;
    }

    public Optional<DishesContainer> getDishOwner(long dishId) {
        for (List<PlanDay> planDays : foodPlanDays.values()) {
            for (PlanDay day : planDays) {
                var dishRef = day.getDishes().stream().filter(dish -> dish.getDishId() == dishId).findFirst();
                if (dishRef.isPresent()) {
                    return Optional.of(day);
                }
                for (Meal meal : day.getMeals()) {
                    dishRef = meal.getDishes().stream().filter(dish -> dish.getDishId() == dishId).findFirst();
                    if (dishRef.isPresent()) {
                        return Optional.of(meal);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public long getPlanIdByDayId(long dayId) {
        long thePlan = -1;
        for (var plan : foodPlanDays.entrySet()) {
            for (PlanDay day : plan.getValue()) {
                if (day.getDayId() == dayId) {
                    thePlan = plan.getKey();
                    break;
                }
            }
        }
        return thePlan;
    }

    public long getPlanIdByMealId(long mealId) {
        long thePlan = -1;
        for (var plan : foodPlanDays.entrySet()) {
            for (PlanDay day : plan.getValue()) {
                for (Meal meal : day.getMeals()) {
                    if (meal.getMealId() == mealId) {
                        return plan.getKey();
                    }
                }
            }
        }
        return thePlan;
    }
}
