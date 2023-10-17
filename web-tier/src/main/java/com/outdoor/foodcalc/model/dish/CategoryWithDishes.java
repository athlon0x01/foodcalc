package com.outdoor.foodcalc.model.dish;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public @Data class CategoryWithDishes {
    private long id;
    private String name;
    private List<DishView> dishes;
}
