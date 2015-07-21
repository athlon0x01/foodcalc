package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Abstract base class for entities contains different sets of products.
 *
 * @author Anton Borovyk
 */
public abstract class ComplexFoodEntity implements ProductsContainer{

    /**
     * Combine all collection of different food entities to complex products collection.
     * @return collection of fields products collection
     */
    protected abstract Collection<Collection<ProductRef>> getProductsCollections();

    /**
     * Collect all products contained in this entity and nested entities and sums their weights
     *
     * @return aggregated products list(product weights are summed).
     */
    @Override
    public Collection<ProductRef> getAllProducts() {
        //map products by Id;
        final Map<Long, List<ProductRef>> productsMap = getProductsCollections().stream().flatMap(Collection::stream)
                .collect(groupingBy(ProductRef::getProductId));
        //summarize weight of each product
        return productsMap.values().stream().map(ProductRef::summarizeWeight).collect(toList());
    }
}
