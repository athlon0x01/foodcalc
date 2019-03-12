package com.outdoor.foodcalc.model;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} class.
 *
 * @author Anton Borovyk.
 */
public class SimpleProductCategory {

    public long id;

    @NotEmpty
    public String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleProductCategory that = (SimpleProductCategory) o;
        return id == that.id &&
            Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
