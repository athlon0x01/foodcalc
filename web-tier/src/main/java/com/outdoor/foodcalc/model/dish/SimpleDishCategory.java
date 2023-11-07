package com.outdoor.foodcalc.model.dish;

import com.outdoor.foodcalc.model.product.SimpleProductCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.dish.DishCategory} class.
 *
 * @author Anton Borovyk.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleDishCategory {

    private long id;

    @NotEmpty
    private String name;
}
