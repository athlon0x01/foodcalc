package com.outdoor.foodcalc.model.product;

import com.outdoor.foodcalc.model.FoodView;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * Simplified view model for {@link com.outdoor.foodcalc.domain.model.product.Product} class.
 *
 * @author Anton Borovyk
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder(toBuilder = true)
public class ProductView extends FoodView {
    @NotEmpty
    private String name;
    @Min(1)
    private long categoryId;
    private Long packageId;
    private String packageName;
}
