package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.service.meal.MealDomainService;
import com.outdoor.foodcalc.model.meal.MealInfo;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.model.meal.MealView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MealService {

    private final MealDomainService mealDomainService;
    private final FoodPackageService foodPackageService;
    private final DishService dishService;
    private final ProductService productService;

    public MealService(MealDomainService mealDomainService, FoodPackageService foodPackageService, DishService dishService, ProductService productService) {
        this.mealDomainService = mealDomainService;
        this.foodPackageService = foodPackageService;
        this.dishService = dishService;
        this.productService = productService;
    }

    public List<MealView> getDayMeals(long planId, long dayId) {
        var packagesNames = foodPackageService.getPlanPackagesNames(planId);
        return mealDomainService.getDayMeals(dayId).stream()
                .map(meal -> mapView(packagesNames, meal))
                .collect(Collectors.toList());
    }

    public MealView getMeal(long planId, long id) {
        var packagesNames = foodPackageService.getPlanPackagesNames(planId);
        return mealDomainService.getMeal(id)
                .map(meal -> mapView(packagesNames, meal))
                .orElseThrow(() -> new NotFoundException("Meal with id = " + id + " wasn't found"));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteMeal(long planId, long id) {
        mealDomainService.deleteMeal(planId, id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public MealInfo addMeal(long planId, long dayId, MealInfo meal) {
        Meal domainMeal = Meal.builder()
                .type(new MealType(meal.getTypeId(), ""))
                .build();
        return mapInfo(mealDomainService.addMeal(planId, dayId, domainMeal));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateMeal(long planId, MealInfo meal) {
        Meal domainMeal = Meal.builder()
                .mealId(meal.getId())
                .type(new MealType(meal.getTypeId(), ""))
                .description(meal.getDescription())
                .dishes(dishService.buildMockDishes(meal.getDishes()))
                .products(productService.buildMockProducts(meal.getProducts()))
                .build();
        mealDomainService.updateMeal(planId, domainMeal);
    }

    MealInfo mapInfo(Meal meal) {
        return MealInfo.builder()
                .id(meal.getMealId())
                .description(meal.getDescription())
                .typeId(meal.getType().getTypeId())
                .build();
    }

    MealView mapView(Map<Long, String> packagesName, Meal meal) {
        FoodDetailsInstance mealDetails = meal.getFoodDetails();
        return MealView.builder()
                .id(meal.getMealId())
                .description(meal.getDescription())
                .type(MealTypeView.builder()
                        .id(meal.getType().getTypeId())
                        .name(meal.getType().getName())
                        .build())
                .products(meal.getProducts().stream()
                        .map(productRef -> productService.mapProductRefWithPackageName(packagesName, productRef))
                        .collect(Collectors.toList()))
                .dishes(meal.getDishes().stream()
                        .map(dish -> dishService.mapDishWithPackages(packagesName, dish))
                        .collect(Collectors.toList()))
                .calorific(mealDetails.getCalorific())
                .carbs(mealDetails.getCarbs())
                .fats(mealDetails.getFats())
                .proteins(mealDetails.getProteins())
                .weight(mealDetails.getWeight())
                .build();
    }
}
