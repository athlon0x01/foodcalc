package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
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
     * @return map of {@link ProductRef} and related dishes Id
     */
    Map<Long, List<ProductRef>> getAllTemplateDishesProducts();

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
    boolean insertDishProducts(Dish dish);

    /**
     * Deletes all {@link ProductRef} for dish.
     *
     * @param dishId dish id
     * @return number of deleted products for dish
     */
    long deleteDishProducts(long dishId);

    Map<Long, List<ProductRef>> getDayProductsForAllDaysInPlan(long planId);

    List<ProductRef> getDayProducts(long dayId);

    boolean insertDayProducts(PlanDay day);

    long deleteDayProducts(long dayId);

    Map<Long, List<ProductRef>> getMealProductsForAllMealsInPlan(long planId);

    Map<Long, List<ProductRef>> getMealProductsForAllMealsInDay(long dayId);

    List<ProductRef> getMealProducts(long mealId);

    boolean insertMealProducts(Meal meal);

    long deleteMealProducts(long mealId);

    Map<Long, List<ProductRef>> getMealDishesProducts(long mealId);

    Map<Long, List<ProductRef>> getDayDishesProducts(long dayId);

    Map<Long, List<ProductRef>> getDishesProductsForAllMealsInDay(long dayId);

    Map<Long, List<ProductRef>> getDishesProductsForAllMealsInPlan(long planId);

    Map<Long, List<ProductRef>> getDishesProductsForAllDaysInPlan(long planId);

    void deleteAllDishProductsForMeal(long mealId);
}
