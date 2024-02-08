package com.outdoor.foodcalc.model;

import com.outdoor.foodcalc.model.product.ProductCategory;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ProductCategory} model validation te
 *
 * @author Anton Borovyk.
 */
public class ProductCategoryTest extends BaseModelValidationTest{

    @Test
    public void validationPassTest() {
        ProductCategory category = ProductCategory.builder()
                .name("Test")
                .build();

        Set<ConstraintViolation<ProductCategory>> constraintViolations = validator.validate(category);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void nullNameTest() {
        ProductCategory category = ProductCategory.builder().build();

        Set<ConstraintViolation<ProductCategory>> constraintViolations = validator.validate(category);

        assertEquals(1, constraintViolations.size());
        assertTrue(constraintCheck(constraintViolations, "name", "{javax.validation.constraints.NotEmpty.message}"));
    }

    @Test
    public void emptyNameTest() {
        ProductCategory category = ProductCategory.builder()
                .name("")
                .build();

        Set<ConstraintViolation<ProductCategory>> constraintViolations = validator.validate(category);

        assertEquals(1, constraintViolations.size());
        assertTrue(constraintCheck(constraintViolations, "name", "{javax.validation.constraints.NotEmpty.message}"));
    }
}