package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.service.meal.MealDomainService;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.meal.MealInfo;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.model.meal.MealView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealDomainService mealDomainService;
    private final DishService dishService;
    private final ProductService productService;

    public MealService(MealDomainService mealDomainService, DishService dishService, ProductService productService) {
        this.mealDomainService = mealDomainService;
        this.dishService = dishService;
        this.productService = productService;
    }

    public List<MealView> getAllMeals(long planId, long dayId) {
        return mealDomainService.getAllMeals(planId, dayId).stream()
                .map(this::mapView)
                .collect(Collectors.toList());
    }

    public MealView getMeal(long planId, long dayId, long id) {
        return mealDomainService.getMeal(planId, dayId, id)
                .map(this::mapView)
                .orElseThrow(() -> new NotFoundException("Meal with id = " + id + " wasn't found"));
    }

    public void deleteMeal(long planId, long dayId, long id) {
        mealDomainService.deleteMeal(planId, dayId, id);
    }

    public MealInfo addMeal(long planId, long dayId, MealInfo meal) {
        Meal domainMeal = Meal.builder()
                .type(new MealType(meal.getTypeId(), ""))
                .build();
        return mapInfo(mealDomainService.addMeal(planId, dayId, domainMeal));
    }

    public void updateMeal(long planId, long dayId, long id, MealInfo meal) {
        Meal domainMeal = Meal.builder()
                .mealId(id)
                .type(new MealType(meal.getTypeId(), ""))
                .description(meal.getDescription())
                .dishes(dishService.buildMockDishes(meal.getDishes()))
                .products(productService.buildMockProducts(meal.getProducts()))
                .build();
        mealDomainService.updateMeal(planId, dayId, domainMeal);
    }

    public DishView addMealDish(long planId, long dayId, long mealId, long id) {
        return dishService.mapDishView(mealDomainService.addMealDish(planId, dayId, mealId, id));
    }

    MealInfo mapInfo(Meal meal) {
        return MealInfo.builder()
                .id(meal.getMealId())
                .description(meal.getDescription())
                .typeId(meal.getType().getTypeId())
                .build();
    }

    MealView mapView(Meal meal) {
        FoodDetailsInstance mealDetails = meal.getFoodDetails();
        return MealView.builder()
                .id(meal.getMealId())
                .description(meal.getDescription())
                .type(MealTypeView.builder()
                        .id(meal.getType().getTypeId())
                        .name(meal.getType().getName())
                        .build())
                .products(meal.getProducts().stream()
                        .map(productService::mapProductRef)
                        .collect(Collectors.toList()))
                .dishes(meal.getDishes().stream()
                        .map(dishService::mapDishView)
                        .collect(Collectors.toList()))
                .calorific(mealDetails.getCalorific())
                .carbs(mealDetails.getCarbs())
                .fats(mealDetails.getFats())
                .proteins(mealDetails.getProteins())
                .weight(mealDetails.getWeight())
                .build();
    }
}
