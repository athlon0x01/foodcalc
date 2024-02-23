package com.outdoor.foodcalc.model.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outdoor.foodcalc.model.EntityView;
import com.outdoor.foodcalc.model.product.ProductItem;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class FoodDayInfo extends EntityView {
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    @Builder.Default
    private List<ProductItem> products = new ArrayList<>();
    @Builder.Default
    private List<Long> dishes = new ArrayList<>();
    @Builder.Default
    private List<Long> meals = new ArrayList<>();
}
