package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.Getter;

import java.util.Collection;

/**
 * Helper class for building food details once from aggregated products list.
 * Its purpose is to avoid same products aggregation multiple times for getting each detail (fats, carbs, etc).
 */
@Getter
public class FoodDetailsInstance {
    //calorific in kCal per 100 gram
    private final float calorific;
    //proteins in gram per 100 gram
    private final float proteins;
    //fats in gram per 100 gram
    private final float fats;
    //carbonates in gram per 100 gram
    private final float carbs;
    //weight in grams
    private final float weight;

    public FoodDetailsInstance(Collection<ProductRef> products) {
        float theCalorific = 0.0f;
        float theProteins = 0.0f;
        float theFats = 0.0f;
        float theCarbs = 0.0f;
        float theWeight = 0.0f;
        for (ProductRef product : products) {
            theCalorific += product.getCalorific();
            theProteins += product.getProteins();
            theFats += product.getFats();
            theCarbs += product.getCarbs();
            theWeight += product.getWeight();
        }
        this.calorific = theCalorific;
        this.proteins = theProteins;
        this.fats = theFats;
        this.carbs = theCarbs;
        this.weight = theWeight;
    }

    public FoodDetailsInstance(FoodDetailsInstance foodDetails, int members) {
        this.calorific = foodDetails.calorific * members;
        this.proteins = foodDetails.proteins * members;
        this.fats = foodDetails.fats * members;
        this.carbs = foodDetails.carbs * members;
        this.weight = foodDetails.weight * members;
    }
}
