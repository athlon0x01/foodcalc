package com.outdoor.foodcalc.model.meal;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.meal.MealType} class.
 *
 * @author Anton Borovyk.
 */
public class MealTypeView {

    public int id;

    @NotEmpty
    public String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealTypeView that = (MealTypeView) o;
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
