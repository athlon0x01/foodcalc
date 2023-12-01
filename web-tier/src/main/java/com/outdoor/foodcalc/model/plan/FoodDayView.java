package com.outdoor.foodcalc.model.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outdoor.foodcalc.model.FoodView;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.meal.MealView;
import com.outdoor.foodcalc.model.product.ProductView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class FoodDayView extends FoodView {
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private String description;
    private List<MealView> meals;
    private List<DishView> dishes;
    private List<ProductView> products;
}
