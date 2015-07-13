package com.outdoor.foodcalc.domain.model;

/**
 * Value Object marker interface.
 *
 * @author Anton Borovyk
 */
public interface IValueObject<T> {

    boolean sameValueAs(T other);
}
