package com.outdoor.foodcalc.model.dish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishProduct {
    @Min(1)
    private long productId;
    @PositiveOrZero
    private float weight;
}
