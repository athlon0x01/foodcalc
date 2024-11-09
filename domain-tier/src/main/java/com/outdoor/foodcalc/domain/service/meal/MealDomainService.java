package com.outdoor.foodcalc.domain.service.meal;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.repository.meal.IMealRepo;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealDomainService {

    private final IFoodPlanRepo planRepo;
    private final IMealRepo mealRepo;
    private final MealTypeDomainService mealTypeDomainService;
    private final ProductDomainService productService;
    private final DishDomainService dishService;

    public MealDomainService(IFoodPlanRepo planRepo, IMealRepo mealRepo, MealTypeDomainService mealTypeDomainService, ProductDomainService productService, DishDomainService dishService) {
        this.planRepo = planRepo;
        this.mealRepo = mealRepo;
        this.mealTypeDomainService = mealTypeDomainService;
        this.productService = productService;
        this.dishService = dishService;
    }

    public List<Meal> getDayMeals(long dayId) {
        List<Meal> meals = mealRepo.getDayMeals(dayId);
        //TODO optimization required
        meals.forEach(this::loadMealContent);
        return meals;
    }

    public Optional<Meal> getMeal(long dayId, long id) {
        return mealRepo.getMeal(id).map(meal -> {
            loadMealContent(meal);
            return meal;
        });
    }

    private void loadMealContent(Meal meal) {
        meal.setDishes(dishService.getMealDishes(meal.getMealId()));
        meal.setProducts(productService.getMealProducts(meal.getMealId()));
    }

    public void deleteMeal(long planId, long dayId, long id) {
        productService.deleteMealProducts(id);
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
        if(!mealRepo.updateMeal(meal)) {
            throw new FoodcalcDomainException("Failed to update meal with id=" + mealId);
        }
        meal.setType(mealType);
        dishService.updateMealDishes(mealId, meal.getDishes());
        productService.updateMealProducts(mealId, meal.getProducts());
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }
}
