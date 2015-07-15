package com.outdoor.foodcalc.domain.model.dish;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.FoodDetails;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Dish entity, like cream soup, lemon tea, etc.
 *
 * @author Anton Borovyk
 */
public class Dish implements IDomainEntity<Dish>, FoodDetails {

    private final int dishId;
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

    public Dish(int dishId, String name, DishCategory category) {
        this.dishId = dishId;
        this.name = name;
        this.category = category;
        this.products = new ArrayList<>();
    }

    public Dish(int dishId, String name, String description, DishCategory category, List<ProductRef> products) {
        this.dishId = dishId;
        this.name = name;
        this.description = description;
        this.category = category;
        this.products = new ArrayList<>(products);
    }

    public int getDishId() {
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

    //function to calculate product details, it sums specified parameters from product list
    private float productDetailsCalculation(Function<ProductRef, Float> sp) {
        return products.stream().map(sp).reduce(FoodDetails::floatSum).get();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return productDetailsCalculation(ProductRef::getCalorific);
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return productDetailsCalculation(ProductRef::getProteins);
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return productDetailsCalculation(ProductRef::getFats);
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return productDetailsCalculation(ProductRef::getCalorific);
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return productDetailsCalculation(ProductRef::getWeight);
    }
}
