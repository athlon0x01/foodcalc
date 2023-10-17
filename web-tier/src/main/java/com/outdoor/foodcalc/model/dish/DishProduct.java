package com.outdoor.foodcalc.model.dish;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public @Data class DishProduct {
    @Min(1)
    private long productId;
    @PositiveOrZero
    private float weight;
}
