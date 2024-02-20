package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Abstract base class for entities contains different sets of products.
 *
 * @author Anton Borovyk
 */
public abstract class ComplexFoodEntity implements FoodDetails {

    /**
     * Compare if two collections contains the same values (it is similar with equals, but uses IDomainEntity#sameValueAs)
     * @param first first collection
     * @param second second collection
     * @return if collections contain the same value objects
     */
    public <T extends IDomainEntity> boolean sameCollectionAs(Collection<T> first, Collection<T> second) {
        if (first == second) return true;
        if (first.size() != second.size()) return false;
        Iterator<T> secondIt = second.iterator();
        for (T firstObj : first) {
            if (!firstObj.sameValueAs(secondIt.next())) return false;
        }
        return true;
    }

    public FoodDetailsInstance getFoodDetails() {
        //map products by Id
        final Map<Long, List<ProductRef>> productsMap = getAllProducts().stream()
                .collect(groupingBy(ProductRef::getProductId));
        //summarize weight of each product
        List<ProductRef> products = productsMap.values().stream()
                .map(this::reduceProducts)
                .collect(toList());
        return new FoodDetailsInstance(products);
    }

    /**
     * Reduce list of the same products by summing the weight
     * @param products not empty product list, that contains same products
     * @return product with summarized weight
     */
    private ProductRef reduceProducts(Collection<ProductRef> products) {
        //get Product entity
        ProductRef product = products.iterator().next();
        //summarize product weight
        int weight = products.stream().mapToInt(ProductRef::getInternalWeight).sum();
        //return updated ProductRef
        return product.buildNewRef(weight);
    }
}
