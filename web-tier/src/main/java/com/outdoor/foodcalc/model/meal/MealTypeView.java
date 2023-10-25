package com.outdoor.foodcalc.model.meal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.meal.MealType} class.
 *
 * @author Anton Borovyk.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealTypeView {

    private int id;

    @NotEmpty
    private String name;
}
