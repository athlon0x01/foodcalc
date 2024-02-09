package com.outdoor.foodcalc.model.dish;

import com.outdoor.foodcalc.model.EntityView;
import com.outdoor.foodcalc.model.product.ProductItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Jacksonized
@SuperBuilder
public class DishInfo extends EntityView {
    @NotEmpty
    private String name;
    @Min(1)
    private long categoryId;
    private List<ProductItem> products;
}
