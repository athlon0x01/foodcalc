package com.outdoor.foodcalc.model.product;

import com.outdoor.foodcalc.model.EntityView;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotEmpty;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} class.
 *
 * @author Anton Borovyk.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class ProductCategoryView extends EntityView {
    @NotEmpty
    private String name;

}
