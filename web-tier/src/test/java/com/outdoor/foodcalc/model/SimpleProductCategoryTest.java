package com.outdoor.foodcalc.model;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * {@link SimpleProductCategory} model validation te
 *
 * @author Anton Borovyk.
 */
public class SimpleProductCategoryTest extends BaseModelValidationTest{

    @Test
    public void validationPassTest() {
        SimpleProductCategory category = new SimpleProductCategory();
        category.name = "Test";

        Set<ConstraintViolation<SimpleProductCategory>> constraintViolations = validator.validate(category);

        assertEquals(0, constraintViolations.size());
    }

    @Test
    public void nullNameTest() {
        SimpleProductCategory category = new SimpleProductCategory();

        Set<ConstraintViolation<SimpleProductCategory>> constraintViolations = validator.validate(category);

        assertEquals(1, constraintViolations.size());
        assertTrue(constraintCheck(constraintViolations, "name", "{javax.validation.constraints.NotEmpty.message}"));
    }

    @Test
    public void emptyNameTest() {
        SimpleProductCategory category = new SimpleProductCategory();
        category.name = "";

        Set<ConstraintViolation<SimpleProductCategory>> constraintViolations = validator.validate(category);

        assertEquals(1, constraintViolations.size());
        assertTrue(constraintCheck(constraintViolations, "name", "{javax.validation.constraints.NotEmpty.message}"));
    }
}