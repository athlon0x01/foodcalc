package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.plan.DayPlan;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.model.dish.DishProduct;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.dish.SimpleDish;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import com.outdoor.foodcalc.model.plan.SimpleFoodDay;
import com.outdoor.foodcalc.model.product.ProductView;
import com.outdoor.foodcalc.service.DishService;
import com.outdoor.foodcalc.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("${spring.data.rest.basePath}/plans/{planId}/days")
public class FoodDayEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(FoodDayEndpoint.class);
    private final MealEndpoint mealEndpoint;
    private final ProductService productService;
    private final DishService dishService;
    private final Map<Long, List<DayPlan>> days;

    public FoodDayEndpoint(MealEndpoint mealEndpoint, ProductService productService, DishService dishService) {
        this.mealEndpoint = mealEndpoint;
        this.productService = productService;
        this.dishService = dishService;
        this.days = new HashMap<>();
        DayPlan day11 = new DayPlan(11L, LocalDate.of(2023, 11, 23), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        day11.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        DayPlan day12 = new DayPlan(12L, LocalDate.of(2023, 11, 24), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        day12.setDescription("Dummy Lorem ipsum");
        DayPlan day21 = new DayPlan(21L, LocalDate.of(2023, 9, 19), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        day21.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        this.days.put(1L, new ArrayList<>(List.of(day11, day12)));
        this.days.put(2L, new ArrayList<>(List.of(day21)));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<FoodDayView> getAllDays(@PathVariable("planId") long planId) {
        LOG.debug("Getting food plan id = {} days", planId);
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
                .description(day.getDescription())
                .meals(mealEndpoint.getAllMeals(day.getDayId()))
                .products(day.getProducts().stream()
                        .map(this::mapProductRef)
                        .collect(Collectors.toList()))
                .dishes(day.getDishes().stream()
                        .map(this::mapDishRef)
                        .collect(Collectors.toList()))
                .calorific(day.getCalorific())
                .carbs(day.getCarbs())
                .fats(day.getFats())
                .proteins(day.getProteins())
                .weight(day.getWeight())
                .build();
    }

    @GetMapping(path = "{id}", produces = APPLICATION_JSON_VALUE)
    public FoodDayView getDay(@PathVariable("planId") long planId,
                              @PathVariable("id") long id) {
        LOG.debug("Getting food plan id = {} day - {}", planId, id);
        return getFirstDay(planId, id).map(this::mapView)
                .orElseThrow(() -> new NotFoundException("Food Day plan with id = " + id + " wasn't found"));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFoodDay(@PathVariable("planId") long planId,
                              @PathVariable("id") long id) {
        LOG.debug("Removing food plan id = {}, day - {}", planId, id);
        var planDays = getDays(planId);
        getFirstDay(planId, id).ifPresent(planDays::remove);
    }

    void addFoodPlan(long planId) {
        days.put(planId, new ArrayList<>());
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public SimpleFoodDay addFoodDay(@PathVariable("planId") long planId,
                                    @RequestBody @Valid SimpleFoodDay foodDay) {
        LOG.debug("Adding new food day, plan - {}", planId);
        var plan = getDays(planId);
        long maxId = plan.stream().map(DayPlan::getDayId).max(Long::compareTo).orElse(1L) + 1L;
        DayPlan dayPlan = new DayPlan(maxId, foodDay.getDate(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        plan.add(dayPlan);
        foodDay.setId(maxId);
        mealEndpoint.addDayMeal(maxId);
        return foodDay;
    }

    @PutMapping(path = "{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFoodDay(@PathVariable("planId") long planId,
                              @PathVariable("id") long id,
                              @RequestBody @Valid SimpleFoodDay foodDay) {
        LOG.debug("Updating food plan id = {}, day - {}", planId, id);
        DayPlan day = getFirstDay(planId, id)
                .orElseThrow(() -> new NotFoundException("Food day id = " + id + " for plan with id = " + planId + " wasn't found"));
        day.setDate(foodDay.getDate());
        day.setDescription(foodDay.getDescription());
        day.setProducts(foodDay.getProducts().stream()
                .map(this::buildProductRef)
                .collect(Collectors.toList()));
        day.setDishes(foodDay.getDishes().stream()
                .map(dishService::getDishRef)
                .collect(Collectors.toList()));
    }

    @PutMapping(path = "{dayId}/dishes/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDayDish(@PathVariable("planId") long planId,
                              @PathVariable("dayId") long dayId,
                              @PathVariable("id") long id,
                              @RequestBody @Valid SimpleDish newDish) {
        LOG.debug("Updating dish id = {}, day - {}", id, dayId);
        DayPlan day = getFirstDay(planId, dayId)
                .orElseThrow(() -> new NotFoundException("Food day id = " + id + " for plan with id = " + planId + " wasn't found"));
        DishRef dishRef = dishService.mapDishRef(newDish);
        for (int i = 0; i < day.getDishes().size(); i++) {
            if (day.getDishes().get(i).getDishId() == dishRef.getDishId()) {
                List<DishRef> dishes = new ArrayList<>(day.getDishes());
                dishes.set(i, dishRef);
                day.setDishes(dishes);
            }
        }
    }

    @PostMapping(path = "{dayId}/dishes/{id}", produces = APPLICATION_JSON_VALUE)
    public DishView addDayDish(@PathVariable("planId") long planId,
                               @PathVariable("dayId") long dayId,
                               @PathVariable("id") long id) {
        LOG.debug("Adding new dish to day - {}", dayId);
        DayPlan day = getFirstDay(planId, dayId)
                .orElseThrow(() -> new NotFoundException("Food day id = " + id + " for plan with id = " + planId + " wasn't found"));
        DishRef dishRef = dishService.getDishRef(id);
        //TODO new dish should be persisted and linked to the meal
        List<DishRef> dishes = new ArrayList<>(day.getDishes());
        dishes.add(dishRef);
        day.setDishes(dishes);
        return mapDishRef(dishRef);
    }

    private DishView mapDishRef(DishRef dish) {
        return DishView.builder()
                .id(dish.getDishId())
                .name(dish.getName())
                .categoryId(dish.getCategoryId())
                .products(dish.getAllProducts().stream()
                        .map(this::mapProductRef)
                        .collect(Collectors.toList()))
                .calorific(dish.getCalorific())
                .proteins(dish.getCalorific())
                .carbs(dish.getCarbs())
                .weight(dish.getWeight())
                .build();
    }

    private ProductView mapProductRef(ProductRef product) {
        //TODO mapping without reloading the product
        final ProductView view = productService.getProduct(product.getProductId());
        view.setWeight(product.getWeight());
        return view;
    }

    private ProductRef buildProductRef(DishProduct product) {
        Product domainProduct = productService.getDomainProduct(product.getProductId());
        return new ProductRef(domainProduct, Math.round(product.getWeight() * 10));
    }
}
