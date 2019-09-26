package com.outdoor.foodcalc.model.product;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

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
