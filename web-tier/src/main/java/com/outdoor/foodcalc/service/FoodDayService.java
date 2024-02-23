package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.service.plan.PlanDayDomainService;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.plan.FoodDayInfo;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodDayService {

    private final PlanDayDomainService dayDomainService;
    private final MealService mealService;
    private final DishService dishService;
    private final ProductService productService;

    public FoodDayService(PlanDayDomainService dayDomainService, MealService mealService, DishService dishService, ProductService productService) {
        this.dayDomainService = dayDomainService;
        this.mealService = mealService;
        this.dishService = dishService;
        this.productService = productService;
    }

    public List<FoodDayView> getAllDays(long planId) {
        return dayDomainService.getAllDays(planId).stream()
                .map(this::mapView)
                .collect(Collectors.toList());
    }

    public FoodDayView getDay(long planId, long id) {
        return dayDomainService.getDay(planId, id)
                .map(this::mapView)
                .orElseThrow(() -> new NotFoundException("Food day with id = " + id + " wasn't found"));
    }

    public void deleteFoodDay(long planId, long id) {
        dayDomainService.deleteFoodDay(planId, id);
    }

    public FoodDayInfo addFoodDay(long planId,
                                  FoodDayInfo foodDay) {
        PlanDay planDay = PlanDay.builder()
                .date(foodDay.getDate())
                .build();
        return mapInfo(dayDomainService.addFoodDay(planId, planDay));
    }

    public void updateFoodDay(long planId, FoodDayInfo foodDay) {
        PlanDay day = PlanDay.builder()
                .dayId(foodDay.getId())
                .date(foodDay.getDate())
                .description(foodDay.getDescription())
                .meals(foodDay.getMeals().stream()
                        .map(mealId -> Meal.builder().mealId(mealId).build())
                        .collect(Collectors.toList()))
                .dishes(dishService.buildMockDishes(foodDay.getDishes()))
                .products(productService.buildMockProducts(foodDay.getProducts()))
                .build();
        dayDomainService.updateFoodDay(planId, day);
    }

    public DishView addDayDish(long planId, long dayId, long id) {
        return dishService.mapDishView(dayDomainService.addDayDish(planId, dayId, id));
    }

    FoodDayInfo mapInfo(PlanDay day) {
        return FoodDayInfo.builder()
                .id(day.getDayId())
                .date(day.getDate())
                .description(day.getDescription())
                .build();
    }

    FoodDayView mapView(PlanDay day) {
        FoodDetailsInstance dayDetails = day.getFoodDetails();
        return FoodDayView.builder()
                .id(day.getDayId())
                .date(day.getDate())
                .description(day.getDescription())
                .meals(day.getMeals().stream()
                        .map(mealService::mapView)
                        .collect(Collectors.toList()))
                .products(day.getProducts().stream()
                        .map(productService::mapProductRef)
                        .collect(Collectors.toList()))
                .dishes(day.getDishes().stream()
                        .map(dishService::mapDishView)
                        .collect(Collectors.toList()))
                .calorific(dayDetails.getCalorific())
                .carbs(dayDetails.getCarbs())
                .fats(dayDetails.getFats())
                .proteins(dayDetails.getProteins())
                .weight(dayDetails.getWeight())
                .build();
    }
}
