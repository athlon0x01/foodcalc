package com.outdoor.foodcalc.model.meal;

import com.outdoor.foodcalc.model.product.SimpleProduct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
public class MealInfo {
    private long id;
    private String description;
    private long typeId;
    private List<SimpleProduct> products;
    private List<Long> dishes;
}
