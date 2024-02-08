package com.outdoor.foodcalc.model.meal;

import com.outdoor.foodcalc.model.EntityView;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.meal.MealType} class.
 *
 * @author Anton Borovyk.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class MealTypeView extends EntityView {
    @NotEmpty
    private String name;
}
