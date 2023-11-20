package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Dish categories, like soups, snacks, hot drinks, etc.
 *
 * @author Anton Borovyk
 */

@Data
@AllArgsConstructor
public class DishCategory implements IDomainEntity<DishCategory> {

    private final long categoryId;
    private String name;

    @Override
    public boolean sameIdentityAs(DishCategory other) {
        return categoryId == other.categoryId;
    }
}
