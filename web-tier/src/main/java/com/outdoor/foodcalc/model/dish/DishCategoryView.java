package com.outdoor.foodcalc.model.dish;

import com.outdoor.foodcalc.model.EntityView;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.dish.DishCategory} class.
 *
 * @author Anton Borovyk.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class DishCategoryView extends EntityView {
    @NotEmpty
    private String name;
}
