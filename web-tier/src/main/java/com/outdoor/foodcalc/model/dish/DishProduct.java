package com.outdoor.foodcalc.model.dish;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

public class DishProduct {
    @Min(1)
    public long productId;
    @PositiveOrZero
    public float weight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DishProduct that = (DishProduct) o;
        return productId == that.productId && Float.compare(that.weight, weight) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, weight);
    }
}
