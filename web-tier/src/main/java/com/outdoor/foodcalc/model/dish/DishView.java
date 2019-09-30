package com.outdoor.foodcalc.model.dish;

import com.outdoor.foodcalc.model.product.ProductView;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class DishView {
    public long id;
    @NotEmpty
    public String name;
    @Min(1)
    public long categoryId;
    public List<ProductView> products;
}
