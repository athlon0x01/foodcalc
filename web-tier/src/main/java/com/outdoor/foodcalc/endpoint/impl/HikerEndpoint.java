package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.model.plan.HikerInfo;
import com.outdoor.foodcalc.service.HikerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans/{planId}/hikers")
public class HikerEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(HikerEndpoint.class);

    private final HikerService hikerService;

    public HikerEndpoint(HikerService hikerService) {
        this.hikerService = hikerService;
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public HikerInfo getHiker(@PathVariable("planId") long planId,
                              @PathVariable("id") long id) {
        LOG.debug("Getting hiker plan id = {} id - {}", planId, id);
        return hikerService.getHiker(id);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHiker(@PathVariable("planId") long planId,
                            @PathVariable("id") long id) {
        LOG.debug("Removing hiker plan id = {} id - {}", planId, id);
        hikerService.deleteHiker(planId, id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public HikerInfo addHiker(@PathVariable("planId") long planId,
                              @RequestBody @Valid HikerInfo hiker) {
        LOG.debug("Adding new hiker id = {}", planId);
        return hikerService.addHiker(planId, hiker);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateHiker(@PathVariable("planId") long planId,
                            @PathVariable("id") long id,
                            @RequestBody @Valid HikerInfo hiker) {
        verifyEntityId(id, hiker);
        LOG.debug("Updating hiker plan id = {} id - {}", planId, id);
        hikerService.updateHiker(planId, hiker);
    }
}
