package com.outdoor.foodcalc.model.meal;

import com.outdoor.foodcalc.model.FoodView;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.product.ProductView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class MealView extends FoodView {
    private String description;
    private MealTypeView type;
    private List<DishView> dishes;
    private List<ProductView> products;
}
