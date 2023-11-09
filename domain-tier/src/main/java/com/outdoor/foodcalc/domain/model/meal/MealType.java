package com.outdoor.foodcalc.domain.model.meal;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Meal type, like breakfast, lunch, etc.
 *
 * @author Anton Borovyk
 */

@Data
@AllArgsConstructor
public class MealType implements IDomainEntity<MealType> {

    private final int typeId;
    private String name;

    @Override
    public boolean sameIdentityAs(MealType other) {
        return typeId == other.typeId;
    }
}
