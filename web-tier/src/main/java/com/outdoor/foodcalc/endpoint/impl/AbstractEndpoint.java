package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.model.EntityView;
import com.outdoor.foodcalc.model.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEndpoint.class);

    void verifyEntityId(long id, EntityView entity) {
        if (id != entity.getId()) {
            LOG.error("Path variable Id = {} doesn't match with request body Id = {}", id, entity.getId());
            throw new ValidationException("Path variable Id = " + id
                    + " doesn't match with request body Id = " + entity.getId());
        }
    }
}
