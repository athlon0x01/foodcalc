package com.outdoor.foodcalc.model;

import com.outdoor.foodcalc.model.product.ProductCategoryView;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ProductCategoryView} model validation te
 *
 * @author Anton Borovyk.
 */
public class ProductCategoryViewTest extends BaseModelValidationTest{

    @Test
    public void validationPassTest() {
        ProductCategoryView category = ProductCategoryView.builder()
                .name("Test")
                .build();

        Set<ConstraintViolation<ProductCategoryView>> constraintViolations = validator.validate(category);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void nullNameTest() {
        ProductCategoryView category = ProductCategoryView.builder().build();

        Set<ConstraintViolation<ProductCategoryView>> constraintViolations = validator.validate(category);

        assertEquals(1, constraintViolations.size());
        assertTrue(constraintCheck(constraintViolations, "name", "{javax.validation.constraints.NotEmpty.message}"));
    }

    @Test
    public void emptyNameTest() {
        ProductCategoryView category = ProductCategoryView.builder()
                .name("")
                .build();

        Set<ConstraintViolation<ProductCategoryView>> constraintViolations = validator.validate(category);

        assertEquals(1, constraintViolations.size());
        assertTrue(constraintCheck(constraintViolations, "name", "{javax.validation.constraints.NotEmpty.message}"));
    }
}