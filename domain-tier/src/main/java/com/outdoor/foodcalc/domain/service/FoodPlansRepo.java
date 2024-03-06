package com.outdoor.foodcalc.domain.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.springframework.stereotype.Component;

import java.util.*;

//TODO temporary holder \ repository for food days \ meals \ dishes
@Deprecated(forRemoval = true)
@Component
public class FoodPlansRepo {

    private final Map<Long, List<Meal>> allMeals = new HashMap<>();
    private final Map<Long, List<Dish>> daysDishes = new HashMap<>();
    private final Map<Long, List<ProductRef>> daysProducts = new HashMap<>();
    private long maxMealId = 10107L;
    private long maxDishId = 1010101L;

    public long getMaxMealIdAndIncrement() {
        return maxMealId++;
    }

    public long getMaxDishIdAndIncrement() {
        return maxDishId++;
    }

    public List<Dish> getDayDishes(long dayId) {
        List<Dish> dishes = daysDishes.get(dayId);
        return dishes == null ? new ArrayList<>() : dishes;
    }

    public List<ProductRef> getDayProducts(long dayId) {
        List<ProductRef> products = daysProducts.get(dayId);
        return products == null ? new ArrayList<>() : products;
    }

    public List<Meal> getDayMeals(long dayId) {
        List<Meal> meals = allMeals.get(dayId);
        return meals == null ? new ArrayList<>() : meals;
    }

    public void setDayDished(long dayId, List<Dish> dishes) {
        daysDishes.put(dayId, new ArrayList<>(dishes));
    }

    public void setDayProducts(long dayId, List<ProductRef> products) {
        daysProducts.put(dayId, new ArrayList<>(products));
    }

    public void setDayMeals(long dayId, List<Meal> meals) {
        allMeals.put(dayId, new ArrayList<>(meals));
    }

    public void deletePlanDay(long id) {
        daysProducts.remove(id);
        daysDishes.remove(id);
        allMeals.remove(id);
    }

    public Meal getMeal(long dayId, long id) {
        return allMeals.get(dayId).stream()
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

    public void updateDayDishes(long dayId, List<Dish> ids) {
        List<Dish> dishes = daysDishes.get(dayId);
        if (dishes != null) {
            List<Dish> newDishes = new ArrayList<>();
            ids.forEach(id -> getDishById(dishes, id.getDishId())
                    .ifPresent(newDishes::add));
            daysDishes.put(dayId, newDishes);
        }
    }

    public long getDayByDish(long dishId) {
        long dayId = -1;
        for (var day : daysDishes.entrySet()) {
            Optional<Dish> first = day.getValue().stream().filter(dish -> dish.getDishId() == dishId).findFirst();
            if (first.isPresent()) {
                dayId = day.getKey();
            }
        }
        return dayId;
    }

    public Optional<Meal> getMealByDish(long dishId) {
            for (var meals : allMeals.values()) {
                for (Meal meal : meals) {
                    var dishRef = meal.getDishes().stream().filter(dish -> dish.getDishId() == dishId).findFirst();
                    if (dishRef.isPresent()) {
                        return Optional.of(meal);
                    }
                }
            }
        return Optional.empty();
    }

    public long getDayIdByMealId(long mealId) {
        long theDay = -1;
        for (var day : allMeals.entrySet()) {
            for (Meal meal : day.getValue()) {
                if (meal.getMealId() == mealId) {
                    return day.getKey();
                }
            }
        }
        return theDay;
    }
}
