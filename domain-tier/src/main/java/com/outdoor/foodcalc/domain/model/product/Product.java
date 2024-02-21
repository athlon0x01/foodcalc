package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.Objects;

/**
 * Product entity (bread, butter, milk, etc.)
 *
 * @author Anton Borovyk
 */

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class Product implements IDomainEntity {

    @EqualsAndHashCode.Include
    private final long productId;
    private String name;
    private ProductCategory category;
    //calorific in kCal per 100 gram
    private float calorific;
    //proteins in gram per 100 gram
    private float proteins;
    //fats in gram per 100 gram
    private float fats;
    //carbonates in gram per 100 gram
    private float carbs;
    //default product item weight in 0.1 grams
    private int defaultWeight;

    public int getDefaultWeightInt() {
        return defaultWeight;
    }

    public float getDefaultWeight() {
        return defaultWeight / 10.f;
    }

    public void setDefaultWeight(float defaultWeight) {
        this.defaultWeight = Math.round(defaultWeight * 10);
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            Product product = (Product) other;

            if (Float.compare(product.calorific, calorific) != 0) return false;
            if (Float.compare(product.proteins, proteins) != 0) return false;
            if (Float.compare(product.fats, fats) != 0) return false;
            if (Float.compare(product.carbs, carbs) != 0) return false;
            if (defaultWeight != product.defaultWeight) return false;
            if (!Objects.equals(name, product.name)) return false;
            return Objects.equals(category, product.category);
        }
        return false;
    }
}
