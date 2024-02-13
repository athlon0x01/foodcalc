package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.meal.MealType;

import java.util.List;
import java.util.Optional;

/**
 * Product Category repository responsible for {@link MealType} persistence.
 *
 * @author Olga Borovyk.
 */
public interface IMealTypeRepo {

    /**
     * Loads all {@link MealType} objects.
     *
     * @return list of meal types
     */
    List<MealType> getMealTypes();

    /**
     * Loads {@link MealType} object by Id
     *
     * @param id meal type Id
     * @return loaded meal type
     */
    Optional<MealType> getMealType(long id);

    /**
     * Add new {@link MealType}.
     *
     * @param mealType meal type to add
     * @return auto generated Id
     */
    long addMealType(MealType mealType);

    /**
     * Updates selected {@link MealType} with new value.
     *
     * @param mealType updated meal type
     * @return if meal type was updated
     */
    boolean updateMealType(MealType mealType);

    /**
     * Removes selected {@link MealType}.
     *
     * @param id meal type Id to delete
     * @return if meal type was deleted
     */
    boolean deleteMealType(long id);

    /**
     * Check if specified {@link MealType} exists.
     *
     * @param id meal type Id to delete
     * @return if meal type exists.
     */
    boolean exist(long id);
}
