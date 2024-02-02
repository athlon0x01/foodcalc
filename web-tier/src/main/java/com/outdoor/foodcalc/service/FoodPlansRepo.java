package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealRef;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.model.plan.DayPlan;
import com.outdoor.foodcalc.domain.model.plan.DayPlanRef;
import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

//TODO temporary holder \ repository for food plans \ days \ meals \ dishes
@Component
public class FoodPlansRepo {

    private final MealTypeDomainService mealTypeService;
    private final Map<Long, FoodPlan> foodPlans = new HashMap<>();
    private long maxPlanId = 2L;
    private long maxDayId = 103L;
    private long maxMealId = 10106L;
    private long maxDishId = 1010101L;

    public FoodPlansRepo(MealTypeDomainService mealTypeService) {
        this.mealTypeService = mealTypeService;
    }

    @PostConstruct
    public void init() {
        var mealTypes = mealTypeService.getMealTypes();
        Random random = new Random();
        List<MealRef> mealRefs1 = List.of(buildRandomMeal(10101L, mealTypes, random), buildRandomMeal(10102L, mealTypes, random));
        List<MealRef> mealRefs2 = List.of(buildRandomMeal(10103L, mealTypes, random), buildRandomMeal(10104L, mealTypes, random));
        List<MealRef> mealRefs3 = List.of(buildRandomMeal(10105L, mealTypes, random), buildRandomMeal(10106L, mealTypes, random));

        DayPlan day11 = new DayPlan(101L, LocalDate.of(2023, 11, 23), mealRefs1, Collections.emptyList(), Collections.emptyList());
        day11.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
        DayPlan day12 = new DayPlan(102L, LocalDate.of(2023, 11, 24), mealRefs2, Collections.emptyList(), Collections.emptyList());
        day12.setDescription("Dummy Lorem ipsum");
        DayPlan day21 = new DayPlan(103L, LocalDate.of(2023, 9, 19), mealRefs3, Collections.emptyList(), Collections.emptyList());
        day21.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");

        FoodPlan planA = new FoodPlan(1L, "Test plan A", "", 2, 5, List.of(new DayPlanRef(day11), new DayPlanRef(day12)));
        FoodPlan planB = new FoodPlan(2L, "Test food plan B", "", 3, 8, List.of(new DayPlanRef(day21)));
        foodPlans.put(1L, planA);
        foodPlans.put(2L, planB);
    }

    private MealRef buildRandomMeal(long id, List<MealType> types, Random random) {
        var type = types.get(random.nextInt(types.size()));
        Meal meal = new Meal(id, type, Collections.emptyList(), Collections.emptyList());
        meal.setDescription("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore " + type.getName());
        return new MealRef(meal);
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
        return Optional.ofNullable(foodPlans.get(id))
                .orElseThrow(() -> new NotFoundException("Food plan with id = " + id + " wasn't found"));
    }

    public Collection<FoodPlan> getAllPlans() {
        return foodPlans.values();
    }

    public void deleteFoodPlan(long id) {
        foodPlans.remove(id);
    }

    public void addFoodPlan(FoodPlan foodPlan) {
        foodPlans.put(maxPlanId++, foodPlan);
    }

    public DayPlanRef getDay(long planId, long id) {
        var plan = getFoodPlan(planId);
        return plan.getDays().stream()
                .filter(day -> id == day.getDayId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Food plan day with id = " + id + " wasn't found"));
    }

    public MealRef getMeal(long planId, long dayId, long id) {
        var day = getDay(planId, dayId);
        return day.getMeals().stream()
                .filter(meal -> id == meal.getMealId())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Meal with id = " + id + " wasn't found"));
    }

    public void updateDayInPlan(FoodPlan plan, DayPlan day, String description) {
        day.setDescription(description);
        for (int i = 0; i < plan.getDays().size(); i++) {
            if (plan.getDays().get(i).getDayId() == day.getDayId()) {
                var days = new ArrayList<>(plan.getDays());
                days.set(i, new DayPlanRef(day));
                plan.setDays(days);
            }
        }
    }

    public void updateMealInDay(FoodPlan plan, DayPlanRef day, Meal meal, String description) {
        meal.setDescription(description);
        var meals = new ArrayList<>(day.getMeals());
        for (int i = 0; i < meals.size(); i++) {
            if (meals.get(i).getMealId() == meal.getMealId()) {
                meals.set(i, new MealRef(meal));
            }
        }
        DayPlan newDay = new DayPlan(day.getDayId(), day.getDate(), meals, day.getDishes(), day.getProducts());
        updateDayInPlan(plan, newDay, day.getDescription());
    }
}
