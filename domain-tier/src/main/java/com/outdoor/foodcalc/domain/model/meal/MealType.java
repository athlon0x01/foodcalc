package com.outdoor.foodcalc.domain.model.meal;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealType)) return false;

        MealType mealType = (MealType) o;

        if (typeId != mealType.typeId) return false;
        return !(name != null ? !name.equals(mealType.name) : mealType.name != null);

    }

    @Override
    public int hashCode() {
        int result = typeId;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
