package com.outdoor.foodcalc.model.dish;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
public class CategoryWithDishes {
    private long id;
    private String name;
    private List<DishView> dishes;
}
