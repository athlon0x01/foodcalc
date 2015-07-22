package com.outdoor.foodcalc.domain.model.layout;

import com.google.common.collect.ImmutableList;
import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Grocery Layout entity represent typical layout for trekking trip splited to days, meals & dishes.
 *
 * @author Anton Borovyk
 */
public class GroceryLayout extends ComplexFoodEntity implements IDomainEntity<GroceryLayout> {

    private final long layoutId;
    private String name;
    private String description;
    private int members;
    private int duration;
    private List<LayoutDayRef> days;

    public GroceryLayout(long layoutId, String name, String description,
                         int members, int duration, Collection<LayoutDayRef> days) {
        this.layoutId = layoutId;
        this.name = name;
        this.description = description;
        this.members = members;
        this.duration = duration;
        this.days = new ArrayList<>(days);
    }

    public long getLayoutId() {
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
     * Combine all collection of different food entities to complex products collection.
     *
     * @return collection of fields products collection
     */
    @Override
    protected Collection<Collection<ProductRef>> getProductsCollections() {
        //collect all day products to one list
        return days.stream().map(LayoutDayRef::getAllProducts).collect(toList());
    }

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        //summarize weight of each product
        final Collection<ProductRef> products = super.getAllProducts();
        //multiply to members count
        products.forEach(p -> p.setWeight(p.getWeight() * members));
        return products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroceryLayout)) return false;

        GroceryLayout that = (GroceryLayout) o;

        if (layoutId != that.layoutId) return false;
        if (members != that.members) return false;
        if (duration != that.duration) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return days.equals(that.days);

    }

    @Override
    public int hashCode() {
        int result = (int) (layoutId ^ (layoutId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + members;
        result = 31 * result + duration;
        return result;
    }
}
