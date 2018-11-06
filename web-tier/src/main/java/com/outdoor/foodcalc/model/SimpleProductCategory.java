package com.outdoor.foodcalc.model;

import javax.validation.constraints.NotEmpty;

/**
 * View model for {@link com.outdoor.foodcalc.domain.model.product.ProductCategory} class.
 *
 * @author Anton Borovyk.
 */
public class SimpleProductCategory {

    public long id;

    @NotEmpty
    public String name;
}
