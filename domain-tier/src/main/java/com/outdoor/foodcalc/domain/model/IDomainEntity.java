package com.outdoor.foodcalc.domain.model;

/**
 * Domain Entity marker interface.
 *
 * @author Anton Borovyk
 */
public interface IDomainEntity<T> {

    boolean sameIdentityAs(T other);
}
