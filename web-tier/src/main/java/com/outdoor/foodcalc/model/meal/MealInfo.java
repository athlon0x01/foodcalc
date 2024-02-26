package com.outdoor.foodcalc.model.meal;

import com.outdoor.foodcalc.model.EntityView;
import com.outdoor.foodcalc.model.product.ProductItem;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class MealInfo extends EntityView {
    private String description;
    private long typeId;
    @Builder.Default
    private List<ProductItem> products = new ArrayList<>();
    @Builder.Default
    private List<Long> dishes = new ArrayList<>();
}
