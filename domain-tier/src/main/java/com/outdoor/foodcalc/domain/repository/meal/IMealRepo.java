package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.meal.Meal;

import java.util.List;
import java.util.Optional;

public interface IMealRepo {

    List<Meal> getAllMeals();

    Optional<Meal> getMeal(long id);

    long addMeal(Meal meal);

    boolean updateMeal(Meal meal);

    boolean deleteMeal(long id);

    boolean existsMeal(long id);
}
