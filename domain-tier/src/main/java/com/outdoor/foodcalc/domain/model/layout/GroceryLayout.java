package com.outdoor.foodcalc.domain.model.layout;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IDomainEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Grocery Layout entity represent typical layout for trekking trip splited to days, meals & dishes.
 *
 * @author Anton Borovyk
 */
public class GroceryLayout implements IDomainEntity<GroceryLayout>, FoodDetails {

    private final int layoutId;
    private String name;
    private String description;
    private int members;
    private int duration;
    private List<LayoutDayRef> days;

    public GroceryLayout(int layoutId, String name, String description,
                         int members, int duration, List<LayoutDayRef> days) {
        this.layoutId = layoutId;
        this.name = name;
        this.description = description;
        this.members = members;
        this.duration = duration;
        this.days = days;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ImmutableList<LayoutDayRef> getDays() {
        return ImmutableList.copyOf(days);
    }

    public void setDays(List<LayoutDayRef> days) {
        this.days = new ArrayList<>(days);
    }

    @Override
    public boolean sameIdentityAs(GroceryLayout other) {
        return layoutId == other.layoutId;
    }

    /**
     * Internal details summary calculation.
     * @param sp - parameter for calculations, f.e. fats, proteins, etc.
     * @return summarized parameter value
     */
    private float detailsCalculation(Function<FoodDetails, Float> sp) {
        return days.stream().map(sp).reduce(FoodDetails::floatSum).get();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return detailsCalculation(FoodDetails::getCalorific);
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return detailsCalculation(FoodDetails::getProteins);
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return detailsCalculation(FoodDetails::getFats);
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return detailsCalculation(FoodDetails::getCarbs);
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return detailsCalculation(FoodDetails::getWeight);
    }
}
