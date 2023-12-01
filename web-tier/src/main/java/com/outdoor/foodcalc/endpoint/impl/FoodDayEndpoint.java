package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.DayPlan;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans/{planId}/days")
public class FoodDayEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FoodDayEndpoint.class);
    private final Map<Long, List<DayPlan>> days;

    public FoodDayEndpoint() {
        this.days = new HashMap<>();
        DayPlan day11 = new DayPlan(11L, LocalDate.of(2023, 11, 23), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        DayPlan day12 = new DayPlan(12L, LocalDate.of(2023, 11, 24), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        DayPlan day21 = new DayPlan(21L, LocalDate.of(2023, 9, 19), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        this.days.put(1L, List.of(day11, day12));
        this.days.put(2L, List.of(day21));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<FoodDayView> getAllDays(@PathVariable("planId") long planId) {
        LOG.debug("Getting all food plans");
        return getDays(planId).stream()
                .map(this::mapView)
                .collect(Collectors.toList());
    }

    private List<DayPlan> getDays(long planId) {
        List<DayPlan> dayPlans = days.get(planId);
        return dayPlans != null ? dayPlans : Collections.emptyList();
    }

    private Optional<DayPlan> getFirstDay(long planId, long id) {
        return getDays(planId).stream()
                .filter(day -> id == day.getDayId())
                .findFirst();
    }

    private FoodDayView mapView(DayPlan day) {
        return FoodDayView.builder()
                .id(day.getDayId())
                .date(day.getDate())
                .build();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public FoodDayView getDay(@PathVariable("planId") long planId,
                                    @PathVariable("id") long id) {
        LOG.debug("Getting food plan id = {}", id);
        return getFirstDay(planId, id).map(this::mapView)
                .orElseThrow(() -> new NotFoundException("Food Day plan with id = " + id + " wasn't found"));
    }
}
