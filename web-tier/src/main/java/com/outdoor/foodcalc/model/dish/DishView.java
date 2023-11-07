package com.outdoor.foodcalc.model.dish;

import com.outdoor.foodcalc.model.product.ProductView;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Jacksonized
@Builder
public class DishView {
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

    private List<ProductView> products;

}
