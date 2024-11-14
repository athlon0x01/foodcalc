package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.plan.IPlanDayRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRefRepo;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.domain.service.meal.MealDomainService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanDayDomainService {

    private final IFoodPlanRepo planRepo;
    private final IPlanDayRepo dayRepo;
    private final IProductRefRepo productRefRepo;
    private final MealDomainService mealService;
    private final DishDomainService dishService;

    public PlanDayDomainService(IFoodPlanRepo planRepo, IPlanDayRepo dayRepo, IProductRefRepo productRefRepo, MealDomainService mealService, DishDomainService dishService) {
        this.planRepo = planRepo;
        this.dayRepo = dayRepo;
        this.productRefRepo = productRefRepo;
        this.mealService = mealService;
        this.dishService = dishService;
    }

    public List<PlanDay> getPlanDays(long planId) {
        List<PlanDay> days = dayRepo.getPlanDays(planId);
        //load products for all days and set them
        Map<Long, List<ProductRef>> daysProducts = productRefRepo.getDayProductsForAllDaysInPlan(planId);
        //load dishes for all days
        Map<Long, List<Dish>> daysDishes = dishService.getDayDishesForAllDaysInPlan(planId);
        //load meals
        Map<Long, List<Meal>> daysMeals = mealService.getMealsForAllDaysInPlan(planId);
        days.forEach(day -> {
            Optional.ofNullable(daysMeals.get(day.getDayId()))
                    .ifPresent(day::setMeals);
            Optional.ofNullable(daysDishes.get(day.getDayId()))
                    .ifPresent(day::setDishes);
            Optional.ofNullable(daysProducts.get(day.getDayId()))
                .ifPresent(day::setProducts);
        });
        return days;
    }

    public Optional<PlanDay> getDay(long planId, long id) {
        return dayRepo.getPlanDay(planId, id)
                .map(day -> {
                    day.setMeals(mealService.getDayMeals(day.getDayId()));
                    day.setDishes(dishService.getDayDishes(day.getDayId()));
                    day.setProducts(productRefRepo.getDayProducts(day.getDayId()));
                    return day;
                });
    }

    public void deleteFoodDay(long planId, long id) {
        productRefRepo.deleteDayProducts(id);
        dishService.deleteDayDishes(id);
        dayRepo.deletePlanDay(planId, id);
        //update days indexes, to have proper index number for new days
        List<PlanDay> planDays = dayRepo.getPlanDays(planId).stream()
                .filter(day -> day.getDayId() != id)
                .collect(Collectors.toList());
        updateDaysOrder(planDays);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }

    private void updateDaysOrder(List<PlanDay> planDays) {
        for (int i = 0; i < planDays.size(); i++) {
            dayRepo.updatePlanDayIndex(planDays.get(i).getDayId(), i);
        }
    }


    public PlanDay addFoodDay(long planId,
                              PlanDay foodDay) {
        long id = dayRepo.addPlanDay(planId, foodDay);
        PlanDay newDay = foodDay.toBuilder()
                .dayId(id)
                .build();
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        return newDay;
    }

    public void updateFoodDay(long planId, PlanDay foodDay) {
        long dayId = foodDay.getDayId();
        if(!dayRepo.existsPlanDay(dayId)) {
            throw new NotFoundException("Plan day with id=" + dayId + " doesn't exist");
        }
        dishService.updateDayDishes(dayId, foodDay.getDishes());
        //updating products of the day
        productRefRepo.deleteDayProducts(dayId);
        if (!productRefRepo.insertDayProducts(foodDay)) {
            throw new FoodcalcDomainException("Failed to add products for day with id=" + dayId);
        }
        //TODO verify if we need it
//        dayRepo.deleteDayMeals(dayId);
//        if (!foodDay.getMeals().isEmpty() && !dayRepo.addMealsToDay(foodDay)) {
//            throw new FoodcalcDomainException("Failed to add meals for day with id=" + dayId);
//        }
        if(!dayRepo.updatePlanDay(foodDay)) {
            throw new FoodcalcDomainException("Failed to plan day with id=" + dayId);
        }
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }
}
