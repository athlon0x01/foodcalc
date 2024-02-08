package com.outdoor.foodcalc.model.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.outdoor.foodcalc.model.product.SimpleProduct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Data
@Jacksonized
@Builder
public class FoodDayInfo {
    private long id;
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private List<SimpleProduct> products;
    private List<Long> dishes;
}
