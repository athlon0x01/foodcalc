package com.outdoor.foodcalc.model.dish;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Jacksonized
@Builder
public class SimpleDish {
    private long id;
    @NotEmpty
    private String name;
    @Min(1)
    private long categoryId;
    private List<DishProduct> products;
}
