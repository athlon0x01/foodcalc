package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.product.ProductRef;

import java.util.List;
import java.util.Map;

/**
 * ProductRef repository responsible for {@link ProductRef} persistence.
 *
 * @author Olga Borovyk
 */
public interface IProductRefRepo {

     /**
     * Loads all {@link ProductRef}
     *
     * @return list of {@link ProductRef} and related dishes Id
     */
    Map<Long, List<ProductRef>> getAllDishProducts();

    /**
     * Loads all {@link ProductRef} of dish.
     *
     * @return list of {@link ProductRef}
     */
    List<ProductRef> getDishProducts(long dishId);

    /**
     * Saves list of {@link ProductRef} for dish.
     *
     * @param dish dish to add its list of products
     * @return if list of  {@link ProductRef} was saved for dish
     */
    boolean addDishProducts(Dish dish);

    /**
     * Deletes all {@link ProductRef} for dish.
     *
     * @param dishId dish id
     * @return if products was deleted for dish
     */
    boolean deleteDishProducts(long dishId);
}
