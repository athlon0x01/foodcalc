package com.outdoor.foodcalc.model.meal;

import com.outdoor.foodcalc.model.dish.DishProduct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
public class SimpleMeal {
    private long id;
    private String description;
    private long typeId;
    private List<DishProduct> products;
    private List<Long> dishes;
}
