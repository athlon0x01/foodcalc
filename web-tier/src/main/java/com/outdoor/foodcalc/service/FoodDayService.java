package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.service.plan.PlanDayDomainService;
import com.outdoor.foodcalc.model.plan.FoodDayInfo;
import com.outdoor.foodcalc.model.plan.FoodDayView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FoodDayService {

    private final PlanDayDomainService dayDomainService;
    private final FoodPackageService foodPackageService;
    private final MealService mealService;
    private final DishService dishService;
    private final ProductService productService;

    public FoodDayService(PlanDayDomainService dayDomainService, FoodPackageService foodPackageService, MealService mealService, DishService dishService, ProductService productService) {
        this.dayDomainService = dayDomainService;
        this.foodPackageService = foodPackageService;
        this.mealService = mealService;
        this.dishService = dishService;
        this.productService = productService;
    }

    public List<FoodDayView> getPlanDays(long planId) {
        var packagesNames = foodPackageService.getPlanPackagesNames(planId);
        return dayDomainService.getPlanDays(planId).stream()
                .map(day-> mapView(packagesNames, day))
                .collect(Collectors.toList());
    }

    public FoodDayView getDay(long planId, long id) {
        var packagesNames = foodPackageService.getPlanPackagesNames(planId);
        return dayDomainService.getDay(planId, id)
                .map(day-> mapView(packagesNames, day))
                .orElseThrow(() -> new NotFoundException("Food day with id = " + id + " wasn't found"));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteFoodDay(long planId, long id) {
        dayDomainService.deleteFoodDay(planId, id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public FoodDayInfo addFoodDay(long planId,
                                  FoodDayInfo foodDay) {
        PlanDay planDay = PlanDay.builder()
                .date(foodDay.getDate())
                .build();
        return mapInfo(dayDomainService.addFoodDay(planId, planDay));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
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

    FoodDayInfo mapInfo(PlanDay day) {
        return FoodDayInfo.builder()
                .id(day.getDayId())
                .date(day.getDate())
                .description(day.getDescription())
                .build();
    }

    FoodDayView mapView(Map<Long, String> packagesName, PlanDay day) {
        FoodDetailsInstance dayDetails = day.getFoodDetails();
        return FoodDayView.builder()
                .id(day.getDayId())
                .date(day.getDate())
                .description(day.getDescription())
                .meals(day.getMeals().stream()
                        .map(meal -> mealService.mapView(packagesName, meal))
                        .collect(Collectors.toList()))
                .products(day.getProducts().stream()
                        .map(productRef -> productService.mapProductRefWithPackageName(packagesName, productRef))
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
