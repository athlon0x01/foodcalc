package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.repository.plan.IPlanDayRepo;
import com.outdoor.foodcalc.domain.service.FoodPlansRepo;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlanDayDomainService {

    private final IPlanDayRepo dayRepo;
    private final ProductDomainService productService;
    private final DishDomainService dishService;
    private final FoodPlansRepo tmpRepo;

    public PlanDayDomainService(IPlanDayRepo dayRepo, ProductDomainService productService, DishDomainService dishService, FoodPlansRepo tmpRepo) {
        this.dayRepo = dayRepo;
        this.productService = productService;
        this.dishService = dishService;
        this.tmpRepo = tmpRepo;
    }

    public List<PlanDay> getAllDays(long planId) {
        return tmpRepo.getFoodPlan(planId).getDays();
    }

    public Optional<PlanDay> getDay(long planId, long id) {
        return Optional.ofNullable(tmpRepo.getDay(planId, id));
    }

    public void deleteFoodDay(long planId, long id) {
        var plan = tmpRepo.getFoodPlan(planId);
        var days = plan.getDays().stream()
                .filter(day -> id != day.getDayId())
                .collect(Collectors.toList());
        plan.setDays(days);
        plan.setLastUpdated(ZonedDateTime.now());
    }

    public PlanDay addFoodDay(long planId,
                              PlanDay foodDay) {
        var plan = tmpRepo.getFoodPlan(planId);
        PlanDay newDay = foodDay.toBuilder()
                .dayId(tmpRepo.getMaxDayIdAndIncrement())
                .build();
        plan.getDays().add(newDay);
        plan.setLastUpdated(ZonedDateTime.now());
        return newDay;
    }

    public void updateFoodDay(long planId, PlanDay foodDay) {
        var plan = tmpRepo.getFoodPlan(planId);
        var oldDay = tmpRepo.getDay(planId, foodDay.getDayId());
        foodDay.setProducts(foodDay.getProducts().stream()
                .map(productService::loadProduct)
                .collect(Collectors.toList()));
        foodDay.setDishes(tmpRepo.reorderDishes(oldDay.getDishes(), foodDay.getDishes()));
        foodDay.setMeals(reorderMeals(oldDay.getMeals(), foodDay.getMeals()));
        tmpRepo.updateDayInPlan(plan, foodDay, foodDay.getDescription());
    }

    private Optional<Meal> getMealById(List<Meal> meals, long id) {
        return meals.stream()
                .filter(dish -> dish.getMealId() == id)
                .findFirst();
    }

    private List<Meal> reorderMeals(List<Meal> meals, List<Meal> ids) {
        List<Meal> newMeals = new ArrayList<>();
        ids.forEach(id -> getMealById(meals, id.getMealId())
                .ifPresent(newMeals::add));
        return newMeals;
    }

    public Dish addDayDish(long planId, long dayId, long id) {
        var plan = tmpRepo.getFoodPlan(planId);
        var day = tmpRepo.getDay(planId, dayId);
        //building new dish as a copy of template dish
        Dish dish = dishService.getDishCopy(id, tmpRepo.getMaxDishIdAndIncrement());
        //TODO new dish should be persisted and linked to the day
        day.getDishes().add(dish);
        plan.setLastUpdated(ZonedDateTime.now());
        return dish;
    }
}
