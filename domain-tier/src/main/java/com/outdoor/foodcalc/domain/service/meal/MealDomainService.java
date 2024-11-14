package com.outdoor.foodcalc.domain.service.meal;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.meal.IMealRepo;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRefRepo;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealDomainService {

    private final IFoodPlanRepo planRepo;
    private final IMealRepo mealRepo;
    private final IProductRefRepo productRefRepo;
    private final MealTypeDomainService mealTypeDomainService;
    private final DishDomainService dishService;

    public MealDomainService(IFoodPlanRepo planRepo, IMealRepo mealRepo, IProductRefRepo productRefRepo, MealTypeDomainService mealTypeDomainService, DishDomainService dishService) {
        this.planRepo = planRepo;
        this.mealRepo = mealRepo;
        this.productRefRepo = productRefRepo;
        this.mealTypeDomainService = mealTypeDomainService;
        this.dishService = dishService;
    }

    public List<Meal> getDayMeals(long dayId) {
        List<Meal> meals = mealRepo.getDayMeals(dayId);
        //load products for all days and set them
        Map<Long, List<ProductRef>> allMealsProducts = productRefRepo.getDayAllMealsProducts(dayId);
        meals.forEach(meal -> {
            //TODO optimization required
            meal.setDishes(dishService.getMealDishes(dayId));
            Optional.ofNullable(allMealsProducts.get(meal.getMealId()))
                    .ifPresent(meal::setProducts);
        });
        return meals;
    }

    public Optional<Meal> getMeal(long id) {
        return mealRepo.getMeal(id).map(meal -> {
            meal.setDishes(dishService.getMealDishes(id));
            meal.setProducts(productRefRepo.getMealProducts(id));
            return meal;
        });
    }

    public void deleteMeal(long planId, long dayId, long id) {
        productRefRepo.deleteMealProducts(id);
        dishService.deleteMealDishes(id);
        mealRepo.detachMeal(id);
        mealRepo.deleteMeal(id);
        //update meals indexes, to have proper index number for new meals
        List<Meal> meals = mealRepo.getDayMeals(dayId).stream()
                .filter(meal -> meal.getMealId() != id)
                .collect(Collectors.toList());
        updateMealsOrder(dayId, meals);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }

    private void updateMealsOrder(long dayId, List<Meal> meals) {
        for (int i = 0; i < meals.size(); i++) {
            mealRepo.updateDayMealIndex(dayId, meals.get(i).getMealId(), i);
        }
    }

    public Meal addMeal(long planId, long dayId, Meal meal) {
        var mealType = mealTypeDomainService.getMealType(meal.getType().getTypeId())
                .orElseThrow(() -> new NotFoundException("Failed to get Meal Type, id = " + meal.getType().getTypeId()));
        long id = mealRepo.addMeal(meal);
        mealRepo.attachMeal(dayId, id);
        Meal newMeal = Meal.builder()
                .mealId(id)
                .type(mealType)
                .build();
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        return newMeal;
    }

    public void updateMeal(long planId, long dayId, Meal meal) {
        var mealType = mealTypeDomainService.getMealType(meal.getType().getTypeId())
                .orElseThrow(() -> new NotFoundException("Failed to get Meal Type, id = " + meal.getType().getTypeId()));
        long mealId = meal.getMealId();
        if(!mealRepo.existsMeal(mealId)) {
            throw new NotFoundException("Meal with id=" + mealId + " doesn't exist");
        }
        //updating products of the day
        productRefRepo.deleteMealProducts(mealId);
        if (!productRefRepo.insertMealProducts(meal)) {
            throw new FoodcalcDomainException("Failed to add products for meal with id=" + mealId);
        }

        dishService.updateMealDishes(mealId, meal.getDishes());
        if(!mealRepo.updateMeal(meal)) {
            throw new FoodcalcDomainException("Failed to update meal with id=" + mealId);
        }
        meal.setType(mealType);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }
}
