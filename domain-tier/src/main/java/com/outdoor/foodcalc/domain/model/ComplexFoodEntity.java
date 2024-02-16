package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Abstract base class for entities contains different sets of products.
 *
 * @author Anton Borovyk
 */
public abstract class ComplexFoodEntity implements ProductsContainer, FoodDetails {

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
        //map products by Id
        final Map<Long, List<ProductRef>> productsMap = getProductsCollections().stream().flatMap(Collection::stream)
                .collect(groupingBy(ProductRef::getProductId));
        //summarize weight of each product
        return productsMap.values().stream().map(ProductRef::summarizeWeight).collect(toList());
    }

    /**
     * Internal function to summarize some product detail, for all products list
     * @param detailsFunction ProductRef function, used for getting values.
     * @return summarized value for product list
     */
    private float internalDetailsCalculation(ToDoubleFunction<ProductRef> detailsFunction) {
        return ((Double) getAllProducts().stream().mapToDouble(detailsFunction).sum()).floatValue();
    }

    /**
     * @return calorific in kCal
     */
    @Override
    public float getCalorific() {
        return internalDetailsCalculation(ProductRef::getCalorific);
    }

    /**
     * @return proteins in gram
     */
    @Override
    public float getProteins() {
        return internalDetailsCalculation(ProductRef::getProteins);
    }

    /**
     * @return fats in gram
     */
    @Override
    public float getFats() {
        return internalDetailsCalculation(ProductRef::getFats);
    }

    /**
     * @return carbonates in gram
     */
    @Override
    public float getCarbs() {
        return internalDetailsCalculation(ProductRef::getCarbs);
    }

    /**
     * @return weight in gram
     */
    @Override
    public float getWeight() {
        return internalDetailsCalculation(ProductRef::getWeight);
    }
}
