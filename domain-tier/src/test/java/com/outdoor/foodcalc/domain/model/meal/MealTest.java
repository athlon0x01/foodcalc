package com.outdoor.foodcalc.domain.model.meal;

import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Meal entity unit test
 *
 * @author Anton Borovyk
 */
public class MealTest {

    private static final double DELTA = 0.00001;
    private List<ProductRef> products;
    private Meal meal;

    @BeforeEach
    public void setup() {
        ProductCategory category = new ProductCategory(222, "Test Category");
        Product buckwheat = Product.builder().productId(123).name("Buckwheat").category(category).
                calorific(317).proteins(8.8f).fats(2.3f).carbs(64.9f).defaultWeight(80).build();
        Product meat = Product.builder().productId(124).name("Meat").category(category).calorific(286)
                .proteins(19.3f).fats(21.5f).carbs(1.7f).defaultWeight(45).build();
        Product onion = Product.builder().productId(125).name("Onion").category(category).calorific(264)
                .proteins(16).fats(0).carbs(47.8f).defaultWeight(5).build();
        Product salt = Product.builder().productId(-1).name("Salt").category(category).build();
        Product potato = Product.builder().productId(126).name("Potato").category(category).calorific(315)
                .proteins(6.1f).fats(0).carbs(72.3f).defaultWeight(15).build();
        Product cookies = Product.builder().productId(127).name("Cookies").category(category).calorific(408)
                .proteins(9.9f).fats(9.8f).carbs(67.7f).defaultWeight(25).build();
        List<ProductRef> dishProducts = new ArrayList<>();
        dishProducts.add(new ProductRef(buckwheat, 700));
        dishProducts.add(new ProductRef(meat, 400));
        dishProducts.add(new ProductRef(onion, 40));
        dishProducts.add(new ProductRef(salt, 25));
        Dish meatCereal = new Dish(223, "Buckwheat meat cereal", "description", new DishCategory(11, "Meat Cereals"), true, dishProducts);
        dishProducts.clear();
        dishProducts.add(new ProductRef(potato, 500));
        dishProducts.add(new ProductRef(meat, 300));
        dishProducts.add(new ProductRef(onion, 50));
        dishProducts.add(new ProductRef(salt, 20));
        Dish meatSoup = new Dish(223, "Buckwheat meat cereal", "description", new DishCategory(11, "Meat Cereals"), true, dishProducts);
        meal = new Meal(321, "", new MealType(21, "Dinner"), Arrays.asList(meatCereal, meatSoup),
                Arrays.asList(new ProductRef(cookies, 300), new ProductRef(salt, 50))) ;
        products = new ArrayList<>();
        products.add(new ProductRef(buckwheat, 700));
        products.add(new ProductRef(meat, 700));
        products.add(new ProductRef(onion, 90));
        products.add(new ProductRef(salt, 95));
        products.add(new ProductRef(cookies, 300));
        products.add(new ProductRef(potato, 500));
        products.sort(Comparator.comparingLong(ProductRef::getProductId));
    }

    @Test
    @Disabled
    public void foodDetailsTest() {
        FoodDetailsInstance details = meal.getFoodDetails();
        //check weight
        assertEquals(238.5f, details.getWeight(), DELTA);

        //check calorific
        assertEquals(725.76f, details.getCalorific(), DELTA);

        //check proteins
        assertEquals(27.13f, details.getProteins(), DELTA);

        //check fats
        assertEquals(19.6f, details.getFats(), DELTA);

        //check carbs
        assertEquals(107.382f, details.getCarbs(), DELTA);
    }

    @Test
    @Disabled
    //TODO fix it
    public void productContainerTest() {
        //check meal products
        List<ProductRef> mealProducts = new ArrayList<>(meal.getAllProducts());
        mealProducts.sort(Comparator.comparingLong(ProductRef::getProductId));
        assertArrayEquals(products.toArray(), mealProducts.toArray());
    }
}
