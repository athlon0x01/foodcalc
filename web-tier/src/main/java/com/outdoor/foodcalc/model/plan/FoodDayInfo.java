package com.outdoor.foodcalc.model.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outdoor.foodcalc.model.EntityView;
import com.outdoor.foodcalc.model.product.SimpleProduct;
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
public class FoodDayInfo extends EntityView {
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private List<SimpleProduct> products;
    private List<Long> dishes;
}
