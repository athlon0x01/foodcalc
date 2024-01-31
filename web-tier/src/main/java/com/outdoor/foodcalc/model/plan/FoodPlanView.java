package com.outdoor.foodcalc.model.plan;

import com.outdoor.foodcalc.model.FoodView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class FoodPlanView extends FoodView {
    private String name;
    private int members;
    private String description;
    private List<FoodDayView> days;
}
