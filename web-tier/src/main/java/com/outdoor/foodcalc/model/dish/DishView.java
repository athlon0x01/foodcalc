package com.outdoor.foodcalc.model.dish;

import com.outdoor.foodcalc.model.FoodView;
import com.outdoor.foodcalc.model.product.ProductView;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder(toBuilder = true)
public class DishView  extends FoodView {
    @NotEmpty
    private String name;
    private String description;
    @Min(1)
    private long categoryId;
    private List<ProductView> products;

}
