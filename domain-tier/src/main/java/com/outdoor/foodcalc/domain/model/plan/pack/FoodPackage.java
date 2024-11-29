package com.outdoor.foodcalc.domain.model.plan.pack;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.Objects;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class FoodPackage implements IDomainEntity {

    @EqualsAndHashCode.Include
    private final long id;
    private String name;
    private String description;
    private float volumeCoefficient;
    //additional package weight in 0.1 grams
    private int additionalWeight;

    public int getAdditionalWeightInt() {
        return additionalWeight;
    }

    public float getAdditionalWeight() {
        return additionalWeight / 10.f;
    }

    public void setAdditionalWeight(float additionalWeight) {
        this.additionalWeight = Math.round(additionalWeight * 10);
    }

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            FoodPackage that = (FoodPackage) other;

            if (!Objects.equals(name, that.name)) return false;
            if (!Objects.equals(description, that.description)) return false;
            if (!Objects.equals(volumeCoefficient, that.volumeCoefficient)) return false;
            return Objects.equals(additionalWeight, that.additionalWeight);
        }
        return false;
    }
}
