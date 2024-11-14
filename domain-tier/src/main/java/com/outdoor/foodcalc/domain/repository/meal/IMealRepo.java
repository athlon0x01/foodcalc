package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.meal.Meal;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IMealRepo {

    Map<Long, List<Meal>> getAllMealsInPlan(long planId);

    List<Meal> getDayMeals(long dayId);

    Optional<Meal> getMeal(long id);

    long addMeal(Meal meal);

    void attachMeal(long dayId, long mealId);

    boolean updateMeal(Meal meal);

    void updateDayMealIndex(long dayId, long mealId, int index);

    boolean deleteMeal(long mealId);

    void detachMeal(long mealId);

    boolean existsMeal(long mealId);
}
