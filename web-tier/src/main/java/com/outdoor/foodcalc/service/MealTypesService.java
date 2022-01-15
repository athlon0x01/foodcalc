package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.meal.MealType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MealTypesService {

    private final List<MealType> mealTypes = new ArrayList<>();

    public List<MealType> getMealTypes() {
        return mealTypes;
    }

    public MealType getMealType(long id) {
        final Optional<MealType> first = mealTypes.stream()
                .filter(c -> c.id == id)
                .findFirst();
        return first.orElseThrow(() -> new NotFoundException(String.valueOf(id)));
    }

    public MealType addMealType(MealType mealType) {
        mealType.id = mealTypes.stream()
                .map(c -> c.id)
                .max(Long::compareTo)
                .orElse((long) mealTypes.size())
                + 1;
        mealTypes.add(mealType);
        return mealType;
    }

    public boolean updateMealType(MealType mealType) {
        final Optional<MealType> first = mealTypes.stream()
                .filter(c -> c.id == mealType.id)
                .findFirst();
        if (!first.isPresent()) {
            return false;
        }
        MealType original = first.get();
        original.name = mealType.name;
        return true;
    }

    public boolean deleteMealType(long id) {
        final Optional<MealType> first = mealTypes.stream()
                .filter(c -> c.id == id)
                .findFirst();
        return first.map(mealTypes::remove)
                .orElse(false);
    }
}
