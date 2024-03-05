package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.meal.Meal;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class MealRepo implements IMealRepo {

    @Override
    public List<Meal> getDayMeals(long dayId) {
        return Collections.emptyList();
    }

    @Override
    public Optional<Meal> getMeal(long dayId, long id) {
        return Optional.empty();
    }

    @Override
    public long addMeal(Meal meal) {
        return 0;
    }

    @Override
    public boolean updateMeal(Meal meal) {
        return false;
    }

    @Override
    public boolean deleteMeal(long id) {
        return false;
    }

    @Override
    public boolean existsMeal(long id) {
        return false;
    }
}
