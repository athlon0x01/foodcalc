package com.outdoor.foodcalc.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class FoodView extends EntityView {
    private float calorific;
    private float proteins;
    private float fats;
    private float carbs;
    private float weight;

    public float getCalorific() {
        return roundToTwoDecimals(calorific);
    }

    public float getProteins() {
        return roundToTwoDecimals(proteins);
    }

    public float getFats() {
        return roundToTwoDecimals(fats);
    }

    public float getCarbs() {
        return roundToTwoDecimals(carbs);
    }

    public float getWeight() {
        return weight;
    }

    private float roundToTwoDecimals(float number) {
        return Math.round(number * 100)/100f;
    }
}
