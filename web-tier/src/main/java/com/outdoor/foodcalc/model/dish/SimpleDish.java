package com.outdoor.foodcalc.model.dish;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Objects;

public class SimpleDish {
    public long id;
    @NotEmpty
    public String name;
    @Min(1)
    public long categoryId;
    public List<DishProduct> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleDish)) return false;
        SimpleDish that = (SimpleDish) o;
        return id == that.id && categoryId == that.categoryId && name.equals(that.name) && products.equals(that.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, categoryId, products);
    }
}
