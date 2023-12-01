package com.outdoor.foodcalc.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@SuperBuilder
public class FoodView {
    private long id;
    private float calorific;
    private float proteins;
    private float fats;
    private float carbs;
    private float weight;
}
