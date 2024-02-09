package com.outdoor.foodcalc.model.plan;

import com.outdoor.foodcalc.model.EntityView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class FoodPlanInfo extends EntityView {
    @NotEmpty
    private String name;
    @Min(1)
    private int members;
    private int duration;
    private String description;
    private List<Long> days;
}
