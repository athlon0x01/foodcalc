package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for all operations with {@link MealType} objects,
 * including transformation domain objects to view models and vise versa.
 *
 * @author Olga Borovyk.
 */
@Service
public class MealTypesService {

    private static final Logger LOG = LoggerFactory.getLogger(MealTypesService.class);

    private final MealTypeDomainService domainService;

    @Autowired
    public MealTypesService(MealTypeDomainService mealTypeDomainService) {
        this.domainService = mealTypeDomainService;
    }

    /**
     * Gets all {@link MealType} objects mapped to {@link MealTypeView}.
     *
     * @return list of meal type views
     */
    public List<MealTypeView> getMealTypes() {
        return domainService.getMealTypes().stream()
                .map(this::mapMealType)
                .collect(Collectors.toList());
    }

    private MealTypeView mapMealType(MealType mealType) {
        return MealTypeView.builder()
                .id(mealType.getTypeId())
                .name(mealType.getName())
                .build();
    }

    /**
     * Gets {@link MealType} objects mapped to {@link MealTypeView}.
     *
     * @param id meal type Id to load
     * @return loaded meal type view
     */
    public MealTypeView getMealType(long id) {
        Optional<MealType> mealType = domainService.getMealType(id);
        if (mealType.isEmpty()) {
            LOG.error("Meal type with id={} wasn't found", id);
            throw new NotFoundException("Meal type wasn't found");
        }
        return mapMealType(mealType.get());
    }

    /**
     * Add new {@link MealType}.
     *
     * @param mealTypeName name of new meal type
     * @return new meal type view
     */
    public MealTypeView addMealType(String mealTypeName) {
        return mapMealType(
                domainService.addMealType(
                        new MealType(-1, mealTypeName)));
    }

    /**
     * Updates selected {@link MealType} with new value.
     *
     * @param model updated meal type view
     */
    public void updateMealType(MealTypeView model) {
        //TODO fix MealType.id
        domainService.updateMealType(new MealType(model.getId(), model.getName()));
    }

    /**
     * Removes selected {@link MealType}.
     *
     * @param id meal type Id to delete
     */
    public void deleteMealType(long id) {
        domainService.deleteMealType(id);
    }
}
