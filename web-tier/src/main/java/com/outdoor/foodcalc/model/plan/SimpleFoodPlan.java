package com.outdoor.foodcalc.model.plan;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@Jacksonized
@Builder
public class SimpleFoodPlan {

    private long id;
    @NotEmpty
    private String name;
    @Min(1)
    private int members;
    @Min(1)
    private int duration;
}
