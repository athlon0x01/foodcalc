package com.outdoor.foodcalc.domain.model.dish;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.ProductsContainer;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * Dish entity, like cream soup, lemon tea, etc.
 *
 * @author Anton Borovyk
 */
public class Dish implements IDomainEntity<Dish>, FoodDetails, ProductsContainer {

    private final long dishId;
    private String name;
    private String description;
    private DishCategory category;
    //dish components
    private List<ProductRef> products;

    public Dish(String name, DishCategory category) {
        this.dishId = -1;
        this.name = name;
        this.category = category;
        this.products = new ArrayList<>();
    }

    public Dish(long dishId, String name, DishCategory category) {
        this.dishId = dishId;
        this.name = name;
        this.category = category;
        this.products = new ArrayList<>();
    }

    public Dish(long dishId, String name, String description, DishCategory category, Collection<ProductRef> products) {
        this.dishId = dishId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.products = new ArrayList<>(products);
    }

    public long getDishId() {
        return dishId;
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

    public DishCategory getCategory() {
        return category;
    }

    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public ImmutableList<ProductRef> getProducts() {
        return ImmutableList.copyOf(products);
    }

    public void setProducts(List<ProductRef> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public boolean sameIdentityAs(Dish other) {
        return dishId == other.dishId;
    }

    /**
     * Internal details summary calculation.
     * @param sp - parameter for calculations, f.e. fats, proteins, etc.
     * @return summarized parameter value
     */
    private float dishDetailsCalculation(Function<FoodDetails, Float> sp) {
        return products.stream().map(sp).reduce(FoodDetails::floatSum).get();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return dishDetailsCalculation(FoodDetails::getCalorific);
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return dishDetailsCalculation(FoodDetails::getProteins);
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return dishDetailsCalculation(FoodDetails::getFats);
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return dishDetailsCalculation(FoodDetails::getCarbs);
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return dishDetailsCalculation(FoodDetails::getWeight);
    }

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        return ImmutableList.copyOf(products);
    }
}
