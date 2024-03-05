package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.meal.Meal;

import java.util.List;
import java.util.Optional;

public interface IMealRepo {

    List<Meal> getDayMeals(long dayId);

    Optional<Meal> getMeal(long dayId, long id);

    long addMeal(Meal meal);

    boolean updateMeal(Meal meal);

    boolean deleteMeal(long id);

    boolean existsMeal(long id);
}
