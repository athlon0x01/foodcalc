package com.outdoor.foodcalc.model.meal;

import com.outdoor.foodcalc.model.EntityView;
import com.outdoor.foodcalc.model.product.SimpleProduct;
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
public class MealInfo extends EntityView {
    private String description;
    private long typeId;
    private List<SimpleProduct> products;
    private List<Long> dishes;
}
