package com.outdoor.foodcalc.model.meal;

import com.outdoor.foodcalc.model.product.SimpleProductCategory;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.meal.MealType} class.
 *
 * @author Anton Borovyk.
 */
public class MealType {

    public long id;

    @NotEmpty
    public String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealType that = (MealType) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return '{' +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
