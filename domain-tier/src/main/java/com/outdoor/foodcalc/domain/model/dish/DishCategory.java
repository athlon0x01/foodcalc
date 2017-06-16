package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.IDomainEntity;

/**
 * Dish categories, like soups, snacks, hot drinks, etc.
 *
 * @author Anton Borovyk
 */
public class DishCategory implements IDomainEntity<DishCategory> {

    private final long categoryId;
    private String name;

    public DishCategory(long categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean sameIdentityAs(DishCategory other) {
        return categoryId == other.categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DishCategory that = (DishCategory) o;

        if (categoryId != that.categoryId) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (categoryId ^ (categoryId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
