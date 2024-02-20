package com.outdoor.foodcalc.domain.model;

/**
 * Domain Entity marker interface.
 *
 * @author Anton Borovyk
 */
public interface IDomainEntity {

    boolean sameValueAs(IDomainEntity other);
}
