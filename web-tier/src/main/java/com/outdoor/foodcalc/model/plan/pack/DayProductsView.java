package com.outdoor.foodcalc.model.plan.pack;

import com.outdoor.foodcalc.model.product.ProductView;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Data
@Jacksonized
@Builder
public class DayProductsView {
    private long dayId;
    private LocalDate date;
    private List<ProductView> products;
}
