package com.outdoor.foodcalc.model.dish;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.dish.DishCategory} class.
 *
 * @author Anton Borovyk.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishCategory {

    private long id;

    @NotEmpty
    private String name;
}
