package com.outdoor.foodcalc.model.plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Data
@Jacksonized
@Builder
public class SimpleFoodDay {
    private long id;
    private String description;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
}
