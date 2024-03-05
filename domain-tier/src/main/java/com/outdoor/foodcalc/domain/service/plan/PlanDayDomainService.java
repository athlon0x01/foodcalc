package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.plan.IPlanDayRepo;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.domain.service.meal.MealDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlanDayDomainService {

    private final IFoodPlanRepo planRepo;
    private final IPlanDayRepo dayRepo;
    private final MealDomainService mealService;
    private final ProductDomainService productService;
    private final DishDomainService dishService;

    public PlanDayDomainService(IFoodPlanRepo planRepo, IPlanDayRepo dayRepo, MealDomainService mealService, ProductDomainService productService, DishDomainService dishService) {
        this.planRepo = planRepo;
        this.dayRepo = dayRepo;
        this.mealService = mealService;
        this.productService = productService;
        this.dishService = dishService;
    }

    public List<PlanDay> getPlanDays(long planId) {
        List<PlanDay> days = dayRepo.getPlanDays(planId);
        //TODO optimization required
        days.forEach(this::loadDayContent);
        return days;
    }

    public Optional<PlanDay> getDay(long planId, long id) {
        return dayRepo.getPlanDay(planId, id)
                .map(day -> {
                    loadDayContent(day);
                    return day;
                });
    }

    private void loadDayContent(PlanDay day) {
        day.setMeals(mealService.getDayMeals(day.getDayId()));
        day.setDishes(dishService.getDayDishes(day.getDayId()));
        day.setProducts(productService.getDayProducts(day.getDayId()));
    }

    public void deleteFoodDay(long planId, long id) {
        productService.deleteDayProducts(id);
        dishService.deleteDayDishes(id);
        dayRepo.deletePlanDay(planId, id);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
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
        productService.updateDayProducts(dayId, foodDay.getProducts());
        dayRepo.deleteDayMeals(dayId);
        if (!foodDay.getMeals().isEmpty() && !dayRepo.addMealsToDay(foodDay)) {
            throw new FoodcalcDomainException("Failed to add meals for day with id=" + dayId);
        }
        if(!dayRepo.updatePlanDayInfo(foodDay)) {
            throw new FoodcalcDomainException("Failed to plan day with id=" + dayId);
        }
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }
}
