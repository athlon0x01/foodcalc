package com.outdoor.foodcalc.model.product;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * Simplified view model for {@link com.outdoor.foodcalc.domain.model.product.Product} class.
 *
 * @author Anton Borovyk
 */
public class SimpleProduct {
    public long id;
    @NotEmpty
    public String name;
    @Min(1)
    public long categoryId;
    public float calorific;
    public float proteins;
    public float fats;
    public float carbs;
    public float defaultWeight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleProduct that = (SimpleProduct) o;
        return id == that.id &&
                categoryId == that.categoryId &&
                Float.compare(that.calorific, calorific) == 0 &&
                Float.compare(that.proteins, proteins) == 0 &&
                Float.compare(that.fats, fats) == 0 &&
                Float.compare(that.carbs, carbs) == 0 &&
                Float.compare(that.defaultWeight, defaultWeight) == 0 &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, categoryId, calorific, proteins, fats, carbs, defaultWeight);
    }

    @Override
    public String toString() {
        return '{' +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", calorific=" + calorific +
                ", proteins=" + proteins +
                ", fats=" + fats +
                ", carbs=" + carbs +
                ", defaultWeight=" + defaultWeight +
                '}';
    }
}
