package com.outdoor.foodcalc.model.dish;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Data
@Jacksonized
@Builder
public class DishProduct {
    @Min(1)
    private long productId;
    @PositiveOrZero
    private float weight;
}
