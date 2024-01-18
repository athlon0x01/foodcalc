package com.outdoor.foodcalc.model.meal;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class SimpleMeal {
    private long id;
    private String description;
    private long typeId;
}
