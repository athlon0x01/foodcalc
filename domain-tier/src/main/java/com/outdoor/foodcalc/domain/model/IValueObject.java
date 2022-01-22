package com.outdoor.foodcalc.domain.model;

import java.util.Collection;
import java.util.Iterator;

/**
 * Value Object marker interface.
 *
 * @author Anton Borovyk
 */
public interface IValueObject<T> {

    boolean sameValueAs(T other);

    /**
     * Compare if two collections contains the same values (it is similar with equals, but uses sameValueAs)
     * @param first first collection
     * @param second second collection
     * @param <T> ValueObject class
     * @return if collections contain the same value objects
     */
    static<T extends IValueObject<T>> boolean sameCollectionAs(Collection<T> first, Collection<T> second) {
        if (first == second) return true;
        if (first.size() != second.size()) return false;
        Iterator<T> secondIt = second.iterator();
        for (T firstObj : first) {
            if (!firstObj.sameValueAs(secondIt.next())) return false;
        }
        return true;
    }
}
