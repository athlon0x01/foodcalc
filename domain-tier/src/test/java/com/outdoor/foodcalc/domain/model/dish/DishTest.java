package com.outdoor.foodcalc.domain.model.dish;

import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Dish entity unit test
 *
 * @author Anton Borovyk
 */
public class DishTest {

    private static final double DELTA = 0.00001;
    private Dish dish;
    private List<ProductRef> products;

    @BeforeEach
    public void setup() {
        products = new ArrayList<>();
        ProductCategory category = new ProductCategory(222, "Test Category");
        Product buckwheat = Product.builder().productId(123).name("Buckwheat").category(category).
                calorific(317).proteins(8.8f).fats(2.3f).carbs(64.9f).defaultWeight(80).build();
        products.add(new ProductRef(buckwheat, 700));
        Product meat = Product.builder().productId(124).name("Meat").category(category).calorific(286)
                .proteins(19.3f).fats(21.5f).carbs(1.7f).defaultWeight(45).build();
        products.add(new ProductRef(meat, 400));
        Product onion = Product.builder().productId(125).name("Onion").category(category).calorific(264)
                .proteins(16).fats(0).carbs(47.8f).defaultWeight(5).build();
        products.add(new ProductRef(onion, 40));
        Product salt = Product.builder().productId(-1).name("Salt").category(category).build();
        products.add(new ProductRef(salt, 25));

        dish = new Dish(123, "Buckwheat meat cereal", "description", new DishCategory(11, "Meat Cereals"), products);
    }

    @Test
    public void foodDetailsTest() {
        FoodDetailsInstance details = dish.getFoodDetails();
        //check weight
        assertEquals (116.5f, details.getWeight(), DELTA);

        //check calorific
        assertEquals(346.86f, details.getCalorific(), DELTA);

        //check proteins
        assertEquals(14.52f, details.getProteins(), DELTA);

        //check fats
        assertEquals(10.21f, details.getFats(), DELTA);

        //check carbs
        assertEquals(48.022f, details.getCarbs(), DELTA);
    }

    @Test
    public void productContainerTest() {
        assertArrayEquals(products.toArray(), dish.getAllProducts().toArray());
    }

    @Test
    void equalsTest() {
        Dish otherDish = Dish.builder().dishId(123).build();
        assertEquals(dish, otherDish);
        assertFalse(dish.sameValueAs(otherDish));
    }
}
