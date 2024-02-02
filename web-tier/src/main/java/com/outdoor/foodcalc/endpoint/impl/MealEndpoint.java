package com.outdoor.foodcalc.endpoint.impl;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishRef;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import com.outdoor.foodcalc.model.dish.DishProduct;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.dish.SimpleDish;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.model.meal.MealView;
import com.outdoor.foodcalc.model.meal.SimpleMeal;
import com.outdoor.foodcalc.model.product.ProductView;
import com.outdoor.foodcalc.service.DishService;
import com.outdoor.foodcalc.service.ProductService;
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
    private final ProductService productService;
    private final DishService dishService;
    private final Map<Long, List<Meal>> meals = new HashMap<>();

    public MealEndpoint(MealTypeDomainService mealTypeService, ProductService productService, DishService dishService) {
        this.mealTypeService = mealTypeService;
        this.productService = productService;
        this.dishService = dishService;
    }

// TODO Use for UI test data generation, but do not commit, because it breaks tests

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
        Meal meal = new Meal(id, type, new ArrayList<>(), new ArrayList<>());
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
                .products(meal.getProducts().stream()
                        .map(this::mapProductRef)
                        .collect(Collectors.toList()))
                .dishes(meal.getDishes().stream()
                        .map(this::mapDishRef)
                        .collect(Collectors.toList()))
                .calorific(meal.getCalorific())
                .carbs(meal.getCarbs())
                .fats(meal.getFats())
                .proteins(meal.getProteins())
                .weight(meal.getWeight())
                .build();
    }

    private ProductView mapProductRef(ProductRef product) {
        //TODO mapping without reloading the product
        final ProductView view = productService.getProduct(product.getProductId());
        view.setWeight(product.getWeight());
        return view;
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
        meal.setProducts(newMeal.getProducts().stream()
                .map(this::buildProductRef)
                .collect(Collectors.toList()));
        meal.setDishes(newMeal.getDishes().stream()
                .map(dishService::getDishRef)
                .collect(Collectors.toList()));
    }

    @PutMapping(path = "{mealId}/dishes/{id}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMealDish(@PathVariable("dayId") long dayId,
                               @PathVariable("mealId") long mealId,
                               @PathVariable("id") long id,
                               @RequestBody @Valid SimpleDish newDish) {
        LOG.debug("Updating dish id = {} meal id = {}, day - {}", id, mealId, dayId);
        Meal meal = getFirstMeal(dayId, mealId)
                .orElseThrow(() -> new NotFoundException("Meal id = " + mealId + " for day id = " + dayId + " wasn't found"));
        DishRef dishRef = dishService.mapDishRef(newDish);
        for (int i=0; i<meal.getDishes().size(); i++) {
            if (meal.getDishes().get(i).getDishId() == dishRef.getDishId()) {
                List<DishRef> dishes = new ArrayList<>(meal.getDishes());
                dishes.set(i, dishRef);
                meal.setDishes(dishes);
            }
        }
    }

    @PostMapping(path = "{mealId}/dishes/{id}", produces = APPLICATION_JSON_VALUE)
    public DishView addMealDish(@PathVariable("dayId") long dayId,
                                @PathVariable("mealId") long mealId,
                                @PathVariable("id") long id) {
        LOG.debug("Adding new dish to meal - {}, day - {}", mealId, dayId);
        Meal meal = getFirstMeal(dayId, mealId)
                .orElseThrow(() -> new NotFoundException("Meal id = " + mealId + " for day id = " + dayId + " wasn't found"));
        DishRef dishRef = dishService.getDishRef(id);
        //TODO new dish should be persisted and linked to the meal
        List<DishRef> dishes = new ArrayList<>(meal.getDishes());
        dishes.add(dishRef);
        meal.setDishes(dishes);
        return mapDishRef(dishRef);
    }

    private ProductRef buildProductRef(DishProduct product) {
        Product domainProduct = productService.getDomainProduct(product.getProductId());
        return new ProductRef(domainProduct, Math.round(product.getWeight() * 10));
    }
}
