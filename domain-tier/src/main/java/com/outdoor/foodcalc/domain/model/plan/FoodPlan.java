package com.outdoor.foodcalc.domain.model.plan;

import com.outdoor.foodcalc.domain.model.ComplexFoodEntity;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.IDomainEntity;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * Food Plan entity represent typical food carrying plan for trekking trip split to days, meals & dishes.
 *
 * @author Anton Borovyk
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class FoodPlan extends ComplexFoodEntity implements IDomainEntity {

    @EqualsAndHashCode.Include
    private final long id;
    private String name;
    private String description;
    private ZonedDateTime createdOn;
    private ZonedDateTime lastUpdated;
    @Builder.Default
    private List<PlanDay> days = new ArrayList<>();
    @Builder.Default
    private List<Hiker> members = new ArrayList<>();

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

            if (!Objects.equals(name, that.name)) return false;
            if (!Objects.equals(description, that.description)) return false;
            if (!Objects.equals(createdOn, that.createdOn)) return false;
            if (!Objects.equals(lastUpdated, that.lastUpdated)) return false;
            if (!sameCollectionAs(members, that.members)) return false;
            return sameCollectionAs(days, that.days);
        }
        return false;
    }

    @Override
    public FoodDetailsInstance getFoodDetails() {
        FoodDetailsInstance foodDetails = super.getFoodDetails();
        return new FoodDetailsInstance(foodDetails, members.size());
    }
}
