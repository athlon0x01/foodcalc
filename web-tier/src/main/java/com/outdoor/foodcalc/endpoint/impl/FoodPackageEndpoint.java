package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.model.plan.pack.FoodDistribution;
import com.outdoor.foodcalc.model.plan.pack.FoodPackageInfo;
import com.outdoor.foodcalc.model.plan.pack.PlanWithPackagesView;
import com.outdoor.foodcalc.service.distribution.FoodDistributionEngine;
import com.outdoor.foodcalc.service.FoodPackageService;
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
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans/{planId}/packages")
public class FoodPackageEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FoodPackageEndpoint.class);

    private final FoodPackageService packageService;
    private final FoodDistributionEngine engine;

    public FoodPackageEndpoint(FoodPackageService packageService, FoodDistributionEngine engine) {
        this.packageService = packageService;
        this.engine = engine;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<FoodPackageInfo> getPlanPackages(@PathVariable("planId") long planId) {
        LOG.debug("Getting packages for plan id = {}", planId);
        return packageService.getPlanPackages(planId);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePackage(@PathVariable("planId") long planId,
                              @PathVariable("id") long id) {
        LOG.debug("Removing package for plan id = {} id - {}", planId, id);
        packageService.deletePackage(planId, id);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public FoodPackageInfo addPackage(@PathVariable("planId") long planId,
                                      @RequestBody @Valid FoodPackageInfo packageInfo) {
        LOG.debug("Adding new package for plan id = {}", planId);
        return packageService.addPackage(planId, packageInfo);
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackage(@PathVariable("planId") long planId,
                              @PathVariable("id") long id,
                              @RequestBody @Valid FoodPackageInfo packageInfo) {
        verifyEntityId(id, packageInfo);
        LOG.debug("Updating package for plan id = {} id = {}", planId, id);
        packageService.updatePackage(planId, packageInfo);
    }

    @GetMapping(path = "/products", produces = APPLICATION_JSON_VALUE)
    public PlanWithPackagesView getPlanProducts(@PathVariable("planId") long planId) {
        LOG.debug("Getting packages with products for plan id = {}", planId);
        return packageService.getPlanPackagesMembers(planId);
    }

    @GetMapping(path = "/best", produces = APPLICATION_JSON_VALUE)
    public FoodPackageInfo getPlanProductsDistribution(@PathVariable("planId") long planId) {
        LOG.debug("Getting packages distribution for plan id = {}", planId);
        FoodDistribution bestDistribution = engine.findBestDistribution(planId);
        return packageService.mapInfo(bestDistribution.getHikerPackages().iterator().next().getPackages().iterator().next().getFoodPackage());
    }
}
