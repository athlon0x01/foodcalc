package com.outdoor.foodcalc.model;

import org.junit.BeforeClass;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * Base class that contains Validator instance and all required utils functions.
 *
 * @author Anton Borovyk.
 */
public abstract class BaseModelValidationTest {

    static Validator validator;

    @BeforeClass
    public static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    <T> boolean constraintCheck(Set<ConstraintViolation<T>> constraints,
                                String field, String constraintTemplate) {
        return constraints.stream()
            .anyMatch(
                constraint -> field.equals(constraint.getPropertyPath().toString())
                    && constraintTemplate.equals(constraint.getMessageTemplate())
            );
    }
}
