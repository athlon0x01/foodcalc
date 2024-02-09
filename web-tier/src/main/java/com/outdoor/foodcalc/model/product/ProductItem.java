package com.outdoor.foodcalc.model.product;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Data
@Jacksonized
@Builder
public class ProductItem {
    @Min(1)
    private long productId;
    @PositiveOrZero
    private float weight;
}
