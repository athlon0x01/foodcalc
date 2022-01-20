package com.outdoor.foodcalc.domain.service.meal;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.repository.meal.IMealTypeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Domain service for all operations with {@link MealType} objects.
 *
 * @author Olga Borovyk.
 */
@Service
public class    MealTypeDomainService {

    private static final Logger LOG = LoggerFactory.getLogger(MealTypeDomainService.class);

    private final IMealTypeRepo mealTypeRepo;

    @Autowired
    public MealTypeDomainService(IMealTypeRepo mealTypeRepo) {
        this.mealTypeRepo = mealTypeRepo;
    }

    /**
     * Loads all {@link MealType} objects.
     *
     * @return list of meal types
     */
    public List<MealType> getMealTypes() {
        return mealTypeRepo.getMealTypes();
    }

    /**
     * Loads {@link MealType} object by Id
     *
     * @param id meal type Id
     * @return loaded meal type
     */
    public Optional<MealType> getMealType(int id) {
        return mealTypeRepo.getMealType(id);
    }

    /**
     * Add new {@link MealType}.
     *
     * @param mealType meal type to add
     * @return instance of new {@link MealType}
     */
    public MealType addMealType(MealType mealType) {
        int id = mealTypeRepo.addMealType(mealType);
        return new MealType(id, mealType.getName());
    }

    /**
     * Updates selected {@link MealType} with new value.
     *
     * @param mealType updated meal type
     * @return if meal type was updated
     */
    public boolean updateMealType(MealType mealType) {
        if (!mealTypeRepo.exist(mealType.getTypeId())) {
            LOG.error("Meal type with id={} doesn't exist", mealType.getTypeId());
            throw new NotFoundException("Meal type doesn't exist");
        }
        return mealTypeRepo.updateMealType(mealType);
    }

    /**
     * Removes selected {@link MealType}.
     *
     * @param id meal type Id to delete
     * @return if meal type was deleted
     */
    public boolean deleteMealType(int id) {
        if (!mealTypeRepo.exist(id)) {
            LOG.error("Meal type with id={} doesn't exist", id);
            throw new NotFoundException("Meal type doesn't exist");
        }
        return mealTypeRepo.deleteMealType(id);
    }
}
