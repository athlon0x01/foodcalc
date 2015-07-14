package com.outdoor.foodcalc.domain.model.layout;

import com.outdoor.foodcalc.domain.model.IDomainEntity;

/**
 * Meal type, like breakfast, lunch, etc.
 *
 * @author Anton Borovyk
 */
public class MealType implements IDomainEntity<MealType> {

    private final int typeId;
    private String name;

    public MealType(int typeId, String name) {
        this.typeId = typeId;
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean sameIdentityAs(MealType other) {
        return typeId == other.typeId;
    }
}
