package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.ProductsContainer;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Dish Value Object, provides readonly access to {@link com.outdoor.foodcalc.domain.model.dish.Dish} Entity.
 *
 * @author Anton Borovyk
 */
public class DishRef implements IValueObject<DishRef>, FoodDetails, ProductsContainer {

    //internal Dish entity
    private final Dish dish;

    public DishRef(Dish dish) {
        if (dish == null)
            throw new IllegalArgumentException("Constructor doesn't allow null parameters!");
        this.dish = dish;
    }

    public long getDishId() {
        return dish.getDishId();
    }

    public String getName() {
        return dish.getName();
    }

    public String getDescription() {
        return dish.getDescription();
    }

    public String getCategoryName() {
        return dish.getCategory().getName();
    }

    public List<ProductRef> getProducts() {
        return Collections.unmodifiableList(dish.getProducts());
    }

    @Override
    public boolean sameValueAs(DishRef other) {
        if (getDishId() != other.getDishId()) return false;
        if (getName() != null ? !getName().equals(other.getName()) : other.getName() != null) return false;
        return IValueObject.sameCollectionAs(getProducts(), other.getProducts());
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return dish.getCalorific();
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return dish.getProteins();
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return dish.getFats();
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return dish.getCarbs();
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return dish.getWeight();
    }

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        return dish.getAllProducts();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DishRef)) return false;

        DishRef dishRef = (DishRef) o;

        return dish.equals(dishRef.dish);

    }

    @Override
    public int hashCode() {
        return dish.hashCode();
    }
}
