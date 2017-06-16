package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Dish entity unit test
 *
 * @author Anton Borovyk
 */
public class DishTest {

    private static final double DELTA = 0.00001;
    private Dish dish;
    private DishRef dishRef;
    private Collection<ProductRef> products;

    @Before
    public void setup() {
        products = new ArrayList<>();
        ProductCategory category = new ProductCategory(222, "Test Category");
        Product buckwheat = new Product(123, "Buckwheat", category, 317, 8.8f, 2.3f, 64.9f, 80);
        products.add(new ProductRef(buckwheat, 700));
        Product meat = new Product(124, "Meat", category, 286, 19.3f, 21.5f, 1.7f, 45);
        products.add(new ProductRef(meat, 400));
        Product onion = new Product(125, "Onion", category, 264, 16, 0, 47.8f, 5);
        products.add(new ProductRef(onion, 40));
        Product salt = new Product("Salt", category);
        products.add(new ProductRef(salt, 25));

        dish = new Dish(123, "Buckwheat meat cereal", "description", new DishCategory(11, "Meat Cereals"), products);
        dishRef = new DishRef(dish);
    }

    @Test
    public void foodDetailsTest() {
        //check weight
        assertEquals(116.5f, dish.getWeight(), DELTA);
        assertEquals(116.5f, dishRef.getWeight(), DELTA);

        //check calorific
        assertEquals(346.86f, dish.getCalorific(), DELTA);
        assertEquals(346.86f, dishRef.getCalorific(), DELTA);

        //check proteins
        assertEquals(14.52f, dish.getProteins(), DELTA);
        assertEquals(14.52f, dishRef.getProteins(), DELTA);

        //check fats
        assertEquals(10.21f, dish.getFats(), DELTA);
        assertEquals(10.21f, dishRef.getFats(), DELTA);

        //check carbs
        assertEquals(48.022f, dish.getCarbs(), DELTA);
        assertEquals(48.022f, dishRef.getCarbs(), DELTA);
    }

    @Test
    public void productContainerTest() {
        assertArrayEquals(products.toArray(), dish.getAllProducts().toArray());
        assertArrayEquals(products.toArray(), dishRef.getAllProducts().toArray());
    }
}
