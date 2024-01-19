package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.model.meal.MealView;
import com.outdoor.foodcalc.model.meal.SimpleMeal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans/{planId}/days/{dayId}/meals")
public class MealEndpoint {
    //TODO add to Dish table `template` column to differentiate template dishes from meal dishes

    private static final Logger LOG = LoggerFactory.getLogger(MealEndpoint.class);

    private final MealTypeDomainService mealTypeService;
    private final Map<Long, List<Meal>> meals = new HashMap<>();

    public MealEndpoint(MealTypeDomainService mealTypeService) {
        this.mealTypeService = mealTypeService;
    }

    @PostConstruct
    public void init() {
        var mealTypes = mealTypeService.getMealTypes();
        Random random = new Random();
        meals.put(11L, new ArrayList<>(List.of(buildRandomMeal(111L, mealTypes, random), buildRandomMeal(112L, mealTypes, random))));
        meals.put(12L, new ArrayList<>(List.of(buildRandomMeal(121L, mealTypes, random), buildRandomMeal(122L, mealTypes, random))));
        meals.put(21L, new ArrayList<>(List.of(buildRandomMeal(211L, mealTypes, random), buildRandomMeal(212L, mealTypes, random))));
    }

    private Meal buildRandomMeal(long id, List<MealType> types, Random random) {
        var type = types.get(random.nextInt(types.size()));
        Meal meal = new Meal(id, type, Collections.emptyList(), Collections.emptyList());
        meal.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore " + type.getName());
        return meal;
    }

    private List<Meal> getMeals(long dayId) {
        List<Meal> dayMeals = meals.get(dayId);
        return dayMeals != null ? dayMeals : Collections.emptyList();
    }

    private Optional<Meal> getFirstMeal(long dayId, long id) {
        return getMeals(dayId).stream()
                .filter(meal -> id == meal.getMealId())
                .findFirst();
    }

    private MealView mapView(Meal meal) {
        return MealView.builder()
                .id(meal.getMealId())
                .description(meal.getDescription())
                .type(new MealTypeView(meal.getType().getTypeId(), meal.getType().getName()))
                .build();
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<MealView> getAllMeals(@PathVariable("dayId") long dayId) {
        LOG.debug("Getting food day id = {} meals", dayId);
        return getMeals(dayId).stream()
                .map(this::mapView)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public MealView getMeal(@PathVariable("dayId") long dayId,
                            @PathVariable("id") long id) {
        LOG.debug("Getting meal = {} day - {}", id, dayId);
        return getFirstMeal(dayId, id).map(this::mapView)
                .orElseThrow(() -> new NotFoundException("Meal with id = " + id + " wasn't found"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeal(@PathVariable("dayId") long dayId,
                           @PathVariable("id") long id) {
        LOG.debug("Removing meal id = {}, day - {}", id, dayId);
        var dayMeals = getMeals(dayId);
        getFirstMeal(dayId, id).ifPresent(dayMeals::remove);
    }

    void addDayMeal(long dayId) {
        meals.put(dayId, new ArrayList<>());
    }

    private MealType getMealType(long id) {
        var mealTypes = mealTypeService.getMealTypes();
        return mealTypes.stream()
                .filter(type -> id == type.getTypeId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MealType with id = " + id + " wasn't found"));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleMeal addMeal(@PathVariable("dayId") long dayId,
                              @RequestBody @Valid SimpleMeal meal) {
        LOG.debug("Adding new meal, day - {}", dayId);
        var dayMeals = getMeals(dayId);
        long maxId = dayMeals.stream().map(Meal::getMealId).max(Long::compareTo).orElse(1L) + 1L;
        Meal newMeal = new Meal(maxId, getMealType(meal.getTypeId()), Collections.emptyList(), Collections.emptyList());
        dayMeals.add(newMeal);
        meal.setId(maxId);
        return meal;
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMeal(@PathVariable("dayId") long dayId,
                           @PathVariable("id") long id,
                           @RequestBody @Valid SimpleMeal newMeal) {
        LOG.debug("Updating meal id = {}, day - {}", id, dayId);
        Meal meal = getFirstMeal(dayId, id)
                .orElseThrow(() -> new NotFoundException("Meal id = " + id + " for day id = " + dayId + " wasn't found"));
        meal.setDescription(newMeal.getDescription());
        if (meal.getType().getTypeId() != newMeal.getTypeId()) {
            meal.setType(getMealType(newMeal.getTypeId()));
        }
    }
}
