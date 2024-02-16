package com.outdoor.foodcalc.domain.model.plan;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Food Plan entity represent typical food carrying plan for trekking trip split to days, meals & dishes.
 *
 * @author Anton Borovyk
 */
public class FoodPlan extends ComplexFoodEntity implements IDomainEntity {

    private final long id;
    private String name;
    private String description;
    private int members;
    private ZonedDateTime createdOn;
    private ZonedDateTime lastUpdated;
    private List<PlanDay> days;

    public FoodPlan(long id, String name, String description,
                    int members, Collection<PlanDay> days) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
        this.days = new ArrayList<>(days);
    }

    public long getId() {
        return id;
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

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getDuration() {
        return days.size();
    }

    public List<PlanDay> getDays() {
        return days;
    }

    public void setDays(List<PlanDay> days) {
        this.days = new ArrayList<>(days);
    }

    /**
     * Combine all collection of different food entities to complex products collection.
     *
     * @return collection of fields products collection
     */
    @Override
    protected Collection<Collection<ProductRef>> getProductsCollections() {
        //collect all day products to one list
        return days.stream().map(PlanDay::getAllProducts).collect(toList());
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
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            FoodPlan that = (FoodPlan) other;

            if (id != that.id) return false;
            if (members != that.members) return false;
            if (!Objects.equals(name, that.name)) return false;
            if (!Objects.equals(description, that.description)) return false;
            if (!Objects.equals(createdOn, that.createdOn)) return false;
            if (!Objects.equals(lastUpdated, that.lastUpdated)) return false;
            return sameCollectionAs(days, that.days);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodPlan that = (FoodPlan) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + members;
        return result;
    }
}
