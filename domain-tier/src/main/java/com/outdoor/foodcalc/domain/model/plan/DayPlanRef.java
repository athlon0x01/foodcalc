package com.outdoor.foodcalc.domain.model.plan;

import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.ProductsContainer;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.meal.MealRef;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Day Plan Value Object, provides readonly access to {@link DayPlan Day Plan} Entity.
 *
 * @author Anton Borovyk
 */
public class DayPlanRef implements IValueObject<DayPlanRef>, FoodDetails, ProductsContainer {

    private final DayPlan day;

    public DayPlanRef(DayPlan day) {
        if (day == null)
            throw new IllegalArgumentException("Constructor doesn't allow null parameters!");
        this.day = day;
    }

    public long getDayId() {
        return day.getDayId();
    }

    public LocalDate getDate() {
        return day.getDate();
    }

    public String getDescription() {
        return day.getDescription();
    }

    public List<MealRef> getMeals() {
        return Collections.unmodifiableList(day.getMeals());
    }

    public List<DishRef> getDishes() {return Collections.unmodifiableList(day.getDishes());}

    public List<ProductRef> getProducts() {
        return Collections.unmodifiableList(day.getProducts());
    }

    @Override
    public boolean sameValueAs(DayPlanRef other) {
        return day.equals(other.day);
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return day.getCalorific();
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return day.getProteins();
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return day.getFats();
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return day.getCarbs();
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return day.getWeight();
    }

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        return day.getAllProducts();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayPlanRef that = (DayPlanRef) o;

        return day.equals(that.day);

    }

    @Override
    public int hashCode() {
        return day.hashCode();
    }
}
