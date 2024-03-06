package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
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

    private final IFoodPlanRepo planRepo;
    private final IPlanDayRepo dayRepo;
    private final ProductDomainService productService;
    private final DishDomainService dishService;
    private final FoodPlansRepo tmpRepo;

    public PlanDayDomainService(IFoodPlanRepo planRepo, IPlanDayRepo dayRepo, ProductDomainService productService, DishDomainService dishService, FoodPlansRepo tmpRepo) {
        this.planRepo = planRepo;
        this.dayRepo = dayRepo;
        this.productService = productService;
        this.dishService = dishService;
        this.tmpRepo = tmpRepo;
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
        day.setMeals(tmpRepo.getDayMeals(day.getDayId()));
        day.setDishes(tmpRepo.getDayDishes(day.getDayId()));
        day.setProducts(tmpRepo.getDayProducts(day.getDayId()));
    }

    public void deleteFoodDay(long planId, long id) {
        tmpRepo.deletePlanDay(id);
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
        if(!dayRepo.updatePlanDayInfo(foodDay)) {
            throw new FoodcalcDomainException("Failed to plan day with id=" + dayId);
        }
        List<ProductRef> products = foodDay.getProducts().stream()
                .map(productService::loadProduct)
                .collect(Collectors.toList());
        tmpRepo.setDayProducts(dayId, products);
        tmpRepo.updateDayDishes(dayId, foodDay.getDishes());
        List<Meal> meals = reorderMeals(tmpRepo.getDayMeals(dayId), foodDay.getMeals());
        tmpRepo.setDayMeals(dayId, meals);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
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
        //building new dish as a copy of template dish
        Dish dish = dishService.getDishCopy(id, tmpRepo.getMaxDishIdAndIncrement());
        //TODO new dish should be persisted and linked to the day
        List<Dish> dayDishes = tmpRepo.getDayDishes(dayId);
        dayDishes.add(dish);
        tmpRepo.setDayDished(dayId, dayDishes);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        return dish;
    }
}
