package com.outdoor.foodcalc.model.dish;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class SimpleDish {
    public long id;
    @NotEmpty
    public String name;
    @Min(1)
    public long categoryId;
    public List<DishProduct> products;
}
