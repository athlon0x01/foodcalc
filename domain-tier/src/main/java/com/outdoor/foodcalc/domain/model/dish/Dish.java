package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.IValueObject;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Dish entity, like cream soup, lemon tea, etc.
 *
 * @author Anton Borovyk
 */
public class Dish extends ComplexFoodEntity implements IDomainEntity<Dish> {

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

    public List<ProductRef> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public void setProducts(List<ProductRef> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public boolean sameIdentityAs(Dish other) {
        return dishId == other.dishId;
    }

    /**
     * For Dish this function in redundant
     *
     * @return collection of fields products collection
     */
    @Override
    protected Collection<Collection<ProductRef>> getProductsCollections() {
        return Collections.emptyList();
    }

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        return Collections.unmodifiableList(products);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dish)) return false;

        Dish dish = (Dish) o;

        if (dishId != dish.dishId) return false;
        if (name != null ? !name.equals(dish.name) : dish.name != null) return false;
        if (category != null ? !category.equals(dish.category) : dish.category != null) return false;
        return IValueObject.sameCollectionAs(products, dish.products);

    }

    @Override
    public int hashCode() {
        int result = (int) (dishId ^ (dishId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
