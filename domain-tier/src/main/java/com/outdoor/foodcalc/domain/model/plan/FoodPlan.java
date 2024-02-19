package com.outdoor.foodcalc.domain.model.plan;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.time.ZonedDateTime;
import java.util.*;

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

    @Override
    public Collection<ProductRef> getAllProducts() {
        List<ProductRef> allProducts = new ArrayList<>();
        days.forEach(day -> allProducts.addAll(day.getAllProducts()));
        return Collections.unmodifiableList(allProducts);
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
