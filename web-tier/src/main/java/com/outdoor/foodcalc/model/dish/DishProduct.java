package com.outdoor.foodcalc.model.dish;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

public class DishProduct {
    @Min(1)
    public long productId;
    @PositiveOrZero
    public float weight;
}
