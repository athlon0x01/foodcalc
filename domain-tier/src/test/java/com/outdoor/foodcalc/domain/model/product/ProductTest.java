package com.outdoor.foodcalc.domain.model.product;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Product & ProductRef unit tests
 *
 * @author Anton Borovyk
 */
public class ProductTest {

    private static final double DELTA = 0.00001;
    private Product product;
    private ProductRef productRef;

    @Before
    public void setup() {
        product = Product.builder().productId(111).name("Corn").description("The Corn")
                .category(new ProductCategory(22, "Cereals"))
                .calorific(119.f).proteins(3.9f).fats(1.3f).carbs(22.7f).defaultWeight(700).build();
        productRef = new ProductRef(product, 800);
    }

    @Test(expected = NullPointerException.class)
    public void productRefNonNullTest() {
        ProductRef productRefNull = new ProductRef(null, 800);
    }
    @Test
    public void testProductWeight() {
        //Product weight should be 0
        assertEquals(0, product.getWeight(), DELTA);
        //ProductRef should contain real weight
        assertEquals(80, productRef.getWeight(), DELTA);
        //ProductRef should contain internal weight in 0.1 grams
        final Method getInternalWeight = ReflectionUtils.findMethod(ProductRef.class, "getInternalWeight");
        ReflectionUtils.makeAccessible(getInternalWeight);
        assertEquals(800, ReflectionUtils.invokeMethod(getInternalWeight, productRef ));
    }

    @Test
    public void testProductCalorific() {
        //Product calorific returns calorific for 100 gram of product
        assertEquals(119, product.getCalorific(), DELTA);
        //ProductRef calorific returns calorific for specified weight
        assertEquals(95.2, productRef.getCalorific(), DELTA);
    }

    @Test
    public void testProductProteins() {
        //Product proteins returns proteins for 100 gram of product
        assertEquals(3.9, product.getProteins(), DELTA);
        //ProductRef proteins returns proteins for specified weight
        assertEquals(3.12, productRef.getProteins(), DELTA);
    }

    @Test
    public void testProductFats() {
        //Product fats returns fats for 100 gram of product
        assertEquals(1.3, product.getFats(), DELTA);
        //ProductRef fats returns fats for specified weight
        assertEquals(1.04, productRef.getFats(), DELTA);
    }

    @Test
    public void testProductCarbs() {
        //Product carbs returns carbs for 100 gram of product
        assertEquals(22.7, product.getCarbs(), DELTA);
        //ProductRef carbs returns carbs for specified weight
        assertEquals(18.16, productRef.getCarbs(), DELTA);
    }
}
