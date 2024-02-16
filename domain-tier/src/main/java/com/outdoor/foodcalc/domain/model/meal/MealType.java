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
public class MealType implements IDomainEntity {

    private final long typeId;
    private String name;

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        return this.equals(other);
    }
}
