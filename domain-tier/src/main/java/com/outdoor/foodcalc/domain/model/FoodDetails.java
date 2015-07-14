package com.outdoor.foodcalc.domain.model;

/**
 * Unified interface for getting food details from any grocery layout object.
 *
 * @author Anton Borovyk
 */
public interface FoodDetails {

    /**
     * @return calorific in kCal
     */
    float getCalorific();

    /**
     * @return proteins in gram
     */
    float getProteins();

    /**
     * @return fats in gram
     */
    float getFats();

    /**
     * @return carbonates in gram
     */
    float getCarbs();

    /**
     * @return weight in gram
     */
    float getWeight();
}
