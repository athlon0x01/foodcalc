package com.outdoor.foodcalc.domain.model;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IValueObjectTest {

    @Test
    public void sameCollectionAs() {
        Product corn = new Product(111, "Corn", "", new ProductCategory(22, "Cereals"),
                119.f, 3.9f, 1.3f, 22.7f, 700);
        ProductRef cornRef = new ProductRef(corn, 800);
        Product apple = new Product(123, "Apple", "", new ProductCategory(11, "Fruit"),
                129.f, 2.9f, 5.3f, 21.7f, 700);
        ProductRef appleRef = new ProductRef(apple, 500);
        List<ProductRef> appleList = Collections.singletonList(appleRef);
        List<ProductRef> cornList = Collections.singletonList(cornRef);
        List<ProductRef> prodList = Arrays.asList(cornRef, appleRef);

        assertTrue(IValueObject.sameCollectionAs(prodList, prodList));
        assertFalse(IValueObject.sameCollectionAs(appleList, prodList));
        assertFalse(IValueObject.sameCollectionAs(appleList, cornList));
        assertTrue(IValueObject.sameCollectionAs(prodList, Arrays.asList(cornRef, appleRef)));
    }
}