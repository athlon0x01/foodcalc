package com.outdoor.foodcalc.domain.model.meal;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Meal entity unit test
 *
 * @author Anton Borovyk
 */
public class MealTest {

    private static final double DELTA = 0.00001;
    private List<ProductRef> products;
    private Meal meal;
    private MealRef mealRef;

    @Before
    public void setup() {
        ProductCategory category = new ProductCategory(222, "Test Category");
        Product buckwheat = new Product(123, "Buckwheat", category, 317, 8.8f, 2.3f, 64.9f, 80);
        Product meat = new Product(124, "Meat", category, 286, 19.3f, 21.5f, 1.7f, 45);
        Product onion = new Product(125, "Onion", category, 264, 16, 0, 47.8f, 5);
        Product salt = new Product("Salt", category);
        Product potato = new Product(126, "Potato", category, 315, 6.1f, 0, 72.3f, 15);
        Product cookies = new Product(127, "Cookies", category, 408, 9.9f, 9.8f, 67.7f, 25);
        Collection<ProductRef> dishProducts = new ArrayList<>();
        dishProducts.add(new ProductRef(buckwheat, 700));
        dishProducts.add(new ProductRef(meat, 400));
        dishProducts.add(new ProductRef(onion, 40));
        dishProducts.add(new ProductRef(salt, 25));
        Dish meatCereal = new Dish(223, "Buckwheat meat cereal", "description", new DishCategory(11, "Meat Cereals"), dishProducts);
        dishProducts.clear();
        dishProducts.add(new ProductRef(potato, 500));
        dishProducts.add(new ProductRef(meat, 300));
        dishProducts.add(new ProductRef(onion, 50));
        dishProducts.add(new ProductRef(salt, 20));
        Dish meatSoup = new Dish(223, "Buckwheat meat cereal", "description", new DishCategory(11, "Meat Cereals"), dishProducts);
        meal = new Meal(321, new MealType(21, "Dinner"), Arrays.asList(new DishRef(meatCereal), new DishRef(meatSoup)),
                Arrays.asList(new ProductRef(cookies, 300), new ProductRef(salt, 50))) ;
        mealRef = new MealRef(meal);
        products = new ArrayList<>();
        products.add(new ProductRef(buckwheat, 700));
        products.add(new ProductRef(meat, 700));
        products.add(new ProductRef(onion, 90));
        products.add(new ProductRef(salt, 95));
        products.add(new ProductRef(cookies, 300));
        products.add(new ProductRef(potato, 500));
        Collections.sort(products);
    }

    @Test
    public void foodDetailsTest() {
        //check weight
        assertEquals(238.5f, meal.getWeight(), DELTA);
        assertEquals(238.5f, mealRef.getWeight(), DELTA);

        //check calorific
        assertEquals(725.76f, meal.getCalorific(), DELTA);
        assertEquals(725.76f, mealRef.getCalorific(), DELTA);

        //check proteins
        assertEquals(27.13f, meal.getProteins(), DELTA);
        assertEquals(27.13f, mealRef.getProteins(), DELTA);

        //check fats
        assertEquals(19.6f, meal.getFats(), DELTA);
        assertEquals(19.6f, mealRef.getFats(), DELTA);

        //check carbs
        assertEquals(107.382f, meal.getCarbs(), DELTA);
        assertEquals(107.382f, mealRef.getCarbs(), DELTA);
    }

    @Test
    public void productContainerTest() {
        //check meal products
        List<ProductRef> mealProducts = (List<ProductRef>) meal.getAllProducts();
        Collections.sort(mealProducts);
        assertArrayEquals(products.toArray(), mealProducts.toArray());

        //check mealRef products
        List<ProductRef> mealRefProducts = (List<ProductRef>) mealRef.getAllProducts();
        Collections.sort(mealRefProducts);
        assertArrayEquals(products.toArray(), mealRefProducts.toArray());
    }
}
