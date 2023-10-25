package com.outdoor.foodcalc.model.dish;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleDish {
    private long id;
    @NotEmpty
    private String name;
    @Min(1)
    private long categoryId;
    private List<DishProduct> products;
}
