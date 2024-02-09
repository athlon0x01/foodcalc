package com.outdoor.foodcalc.model;

import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@RequiredArgsConstructor
@Jacksonized
@SuperBuilder
public class EntityView {
    private long id;
}
