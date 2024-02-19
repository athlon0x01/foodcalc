package com.outdoor.foodcalc.domain.model.product;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Product entity (bread, butter, milk, etc.)
 *
 * @author Anton Borovyk
 */

@Data
@AllArgsConstructor
@Jacksonized
@Builder(toBuilder = true)
public class Product implements IDomainEntity {

    private final long productId;
    private String name;
    private String description;
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
        //TODO fix it, make equals lighter
        return this.equals(other);
    }
}
