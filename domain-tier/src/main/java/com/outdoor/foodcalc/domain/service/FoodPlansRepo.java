package com.outdoor.foodcalc.domain.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.DishesContainer;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

//TODO temporary holder \ repository for food plans \ days \ meals \ dishes
@Deprecated(forRemoval = true)
@Component
public class FoodPlansRepo {

    private final MealTypeDomainService mealTypeService;
    private final Map<Long, FoodPlan> foodPlans = new HashMap<>();
    private long maxPlanId = 3L;
    private long maxDayId = 104L;
    private long maxMealId = 10107L;
    private long maxDishId = 1010101L;

    public FoodPlansRepo(MealTypeDomainService mealTypeService) {
        this.mealTypeService = mealTypeService;
    }

    @PostConstruct
    public void init() {
        //dummy initialization for UI
//        var mealTypes = mealTypeService.getMealTypes();
//        Random random = new Random();
//        List<Meal> mealRefs1 = new ArrayList<>(List.of(buildRandomMeal(10101L, mealTypes, random), buildRandomMeal(10102L, mealTypes, random)));
//        List<Meal> mealRefs2 = new ArrayList<>(List.of(buildRandomMeal(10103L, mealTypes, random), buildRandomMeal(10104L, mealTypes, random)));
//        List<Meal> mealRefs3 = new ArrayList<>(List.of(buildRandomMeal(10105L, mealTypes, random), buildRandomMeal(10106L, mealTypes, random)));
        List<Meal> mealRefs1 = new ArrayList<>();
        List<Meal> mealRefs2 = new ArrayList<>();
        List<Meal> mealRefs3 = new ArrayList<>();

        PlanDay day11 = new PlanDay(101L, LocalDate.of(2023, 11, 23), "", mealRefs1, new ArrayList<>(), new ArrayList<>());
        day11.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        PlanDay day12 = new PlanDay(102L, LocalDate.of(2023, 11, 24), "", mealRefs2, new ArrayList<>(), new ArrayList<>());
        day12.setDescription("Dummy Lorem ipsum");
        PlanDay day21 = new PlanDay(103L, LocalDate.of(2023, 9, 19), "", mealRefs3, new ArrayList<>(), new ArrayList<>());
        day21.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        var now = ZonedDateTime.now();
        FoodPlan planA = new FoodPlan(1L, "Test plan A", "", 2, now, now, new ArrayList<>(List.of(day11, day12)));
        FoodPlan planB = new FoodPlan(2L, "Test food plan B", "", 3, now, now, new ArrayList<>(List.of(day21)));
        foodPlans.put(1L, planA);
        foodPlans.put(2L, planB);
    }

    private Meal buildRandomMeal(long id, List<MealType> types, Random random) {
        var type = types.get(random.nextInt(types.size()));
        Meal meal = new Meal(id, "", type, new ArrayList<>(), new ArrayList<>());
        meal.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore " + type.getName());
        return meal;
    }

    public long getMaxPlanIdAndIncrement() {
        return maxPlanId++;
    }

    public long getMaxDayIdAndIncrement() {
        return maxDayId++;
    }

    public long getMaxMealIdAndIncrement() {
        return maxMealId++;
    }

    public long getMaxDishIdAndIncrement() {
        return maxDishId++;
    }

    public FoodPlan getFoodPlan(long id) {
        return foodPlans.get(id);
    }

    public Collection<FoodPlan> getAllPlans() {
        return foodPlans.values();
    }

    public void deleteFoodPlan(long id) {
        foodPlans.remove(id);
    }

    public void addFoodPlan(FoodPlan foodPlan) {
        foodPlans.put(foodPlan.getId(), foodPlan);
    }

    public void updateFoodPlan(FoodPlan foodPlan) {
        foodPlans.put(foodPlan.getId(), foodPlan);
    }

    public PlanDay getDay(long planId, long id) {
        var plan = getFoodPlan(planId);
        return plan.getDays().stream()
                .filter(day -> id == day.getDayId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Food plan day with id = " + id + " wasn't found"));
    }

    public Meal getMeal(long planId, long dayId, long id) {
        var day = getDay(planId, dayId);
        return day.getMeals().stream()
                .filter(meal -> id == meal.getMealId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Meal with id = " + id + " wasn't found"));
    }

    public void updateDayInPlan(FoodPlan plan, PlanDay day, String description) {
        day.setDescription(description);
        var planDays = plan.getDays();
        for (int i = 0; i < planDays.size(); i++) {
            if (planDays.get(i).getDayId() == day.getDayId()) {
                planDays.set(i, day);
            }
        }
        plan.setLastUpdated(ZonedDateTime.now());
    }

    public void updateMealInDay(FoodPlan plan, PlanDay day, Meal meal, String description) {
        meal.setDescription(description);
        var meals = day.getMeals();
        for (int i = 0; i < meals.size(); i++) {
            if (meals.get(i).getMealId() == meal.getMealId()) {
                meals.set(i, meal);
            }
        }
        plan.setLastUpdated(ZonedDateTime.now());
    }

    public Optional<Dish> getDishById(List<Dish> dishes, long id) {
        return dishes.stream()
                .filter(dish -> dish.getDishId() == id)
                .findFirst();
    }

    public List<Dish> reorderDishes(List<Dish> dishes, List<Dish> ids) {
        List<Dish> newDishes = new ArrayList<>();
        ids.forEach(id -> getDishById(dishes, id.getDishId())
                .ifPresent(newDishes::add));
        return newDishes;
    }

    public Optional<DishesContainer> getDishOwner(long dishId) {
        for (FoodPlan plan : foodPlans.values()) {
            for (PlanDay day : plan.getDays()) {
                var dishRef = day.getDishes().stream().filter(dish -> dish.getDishId() == dishId).findFirst();
                if (dishRef.isPresent()) {
                    return Optional.of(day);
                }
                for (Meal meal : day.getMeals()) {
                    dishRef = meal.getDishes().stream().filter(dish -> dish.getDishId() == dishId).findFirst();
                    if (dishRef.isPresent()) {
                        return Optional.of(meal);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public FoodPlan getPlanByDayId(long dayId) {
        FoodPlan thePlan = null;
        for (FoodPlan plan : foodPlans.values()) {
            for (PlanDay day : plan.getDays()) {
                if (day.getDayId() == dayId) {
                    thePlan = plan;
                    break;
                }
            }
        }
        return thePlan;
    }

    public PlanDay getDayByMealId(long mealId) {
        for (FoodPlan plan : foodPlans.values()) {
            for (PlanDay day : plan.getDays()) {
                for (Meal meal : day.getMeals()) {
                    if (meal.getMealId() == mealId) {
                        return day;
                    }
                }
            }
        }
        return null;
    }
}
