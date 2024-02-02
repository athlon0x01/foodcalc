package com.outdoor.foodcalc.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Setter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class FoodView {
    private long id;
    private float calorific;
    private float proteins;
    private float fats;
    private float carbs;
    private float weight;

    public long getId() {
        return id;
    }

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
