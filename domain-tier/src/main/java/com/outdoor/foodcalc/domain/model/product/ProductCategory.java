package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Product categories, like bakery, meat, etc.
 *
 * @author Anton Borovyk
 */

@Data
@AllArgsConstructor
public class ProductCategory implements IDomainEntity {

    private final long categoryId;
    private String name;

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        return this.equals(other);
    }
}
