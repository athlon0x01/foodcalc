package com.outdoor.foodcalc.model.dish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryWithDishes {
    private long id;
    private String name;
    private List<DishView> dishes;
}
