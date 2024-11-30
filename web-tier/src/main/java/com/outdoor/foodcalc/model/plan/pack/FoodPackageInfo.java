package com.outdoor.foodcalc.model.plan.pack;

import com.outdoor.foodcalc.model.EntityView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class FoodPackageInfo extends EntityView {
    @NotEmpty
    private String name;
    private String description;
    @Positive
    private float volumeCoefficient;
    @Positive
    private float additionalWeight;
}
