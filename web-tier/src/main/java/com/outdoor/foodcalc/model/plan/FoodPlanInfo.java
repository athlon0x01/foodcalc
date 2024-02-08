package com.outdoor.foodcalc.model.plan;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Jacksonized
@Builder
public class FoodPlanInfo {

    private long id;
    @NotEmpty
    private String name;
    @Min(1)
    private int members;
    private int duration;
    private String description;
    private List<Long> days;
}
