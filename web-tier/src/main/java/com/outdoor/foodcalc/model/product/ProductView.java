package com.outdoor.foodcalc.model.product;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Simplified view model for {@link com.outdoor.foodcalc.domain.model.product.Product} class.
 *
 * @author Anton Borovyk
 */
@Data
@Jacksonized
@Builder
public class ProductView {
    private long id;
    @NotEmpty
    private String name;
    @Min(1)
    private long categoryId;
    private float calorific;
    private float proteins;
    private float fats;
    private float carbs;
    private float weight;
}
