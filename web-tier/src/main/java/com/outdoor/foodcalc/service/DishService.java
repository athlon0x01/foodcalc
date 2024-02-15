package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.model.dish.CategoryWithDishes;
import com.outdoor.foodcalc.model.dish.DishCategoryView;
import com.outdoor.foodcalc.model.dish.DishInfo;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.product.ProductView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishDomainService dishDomainService;
    private final DishCategoryDomainService dishCategoryDomainService;
    private final DishCategoryService dishCategoryService;
    private final ProductService productService;
    private final FoodPlansRepo repository;

    @Autowired
    public DishService(DishDomainService dishDomainService, DishCategoryDomainService dishCategoryDomainService, DishCategoryService dishCategoryService, ProductService productService, FoodPlansRepo repository) {
        this.dishDomainService = dishDomainService;
        this.dishCategoryDomainService = dishCategoryDomainService;
        this.dishCategoryService = dishCategoryService;
        this.productService = productService;
        this.repository = repository;
    }

    /**
     * Gets all {@link Dish} objects mapped to {@link DishView}.
     *
     * @return list of products
     */
    public List<CategoryWithDishes> getAllDishes() {
        //load dishes & categories
        final List<DishCategoryView> categories = dishCategoryService.getDishCategories();
        final List<Dish> domainDishes = dishDomainService.getAllDishes();
        //group dishes by categories
        final Map<Long, List<Dish>> dishesMap = domainDishes.stream()
                .collect(Collectors.groupingBy(d -> d.getCategory().getCategoryId()));
        //map domain classes to UI model
        return mapCategoryWithDishes(categories, dishesMap);
    }

    private List<CategoryWithDishes> mapCategoryWithDishes(List<DishCategoryView> categories, Map<Long, List<Dish>> dishesMap) {
        return categories.stream()
                .map(c -> {
                    final List<Dish> dishList = dishesMap.get(c.getId());
                    return CategoryWithDishes.builder()
                            .id(c.getId())
                            .name(c.getName())
                            .dishes((dishList == null) ? new ArrayList<>() : dishList.stream()
                                    .map(this::mapDishView)
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Gets all {@link Dish} objects mapped to {@link DishView}.
     *
     * @param id dishId to load
     * @return loaded dish
     */
    public DishView getDish(long id) {
        Dish domainDish = dishDomainService.getDish(id)
                .orElseThrow(() ->
                        new NotFoundException("Dish wasn't found"));
        return mapDishView(domainDish);
    }

    /**
     * Adds new {@link Dish}.
     *
     * @param dishInfo dish to add
     * @return new dish
     */
    public DishInfo addDish(DishInfo dishInfo) {
        final DishCategory category = dishCategoryDomainService.getCategory(dishInfo.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Dish Category, id = " + dishInfo.getCategoryId()));

        Dish dishToAdd = new Dish( -1, dishInfo.getName(), "", category, mapProductRefs(dishInfo));
        dishInfo.setId(dishDomainService.addDish(dishToAdd).getDishId());
        return dishInfo;
    }

    /**
     * Updates selected {@link Dish} with new value.
     *
     * @param dishInfo updated
     */
    public void updateDish(DishInfo dishInfo) {
        final DishCategory category = dishCategoryDomainService.getCategory(dishInfo.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Dish Category, id = " + dishInfo.getCategoryId() ));

        Dish updatedDish = new Dish(dishInfo.getId(), dishInfo.getName(), "", category, mapProductRefs(dishInfo));
        //TODO will de changed latter
        var dishOwner = repository.getDishOwner(updatedDish.getDishId());
        if (dishOwner.isPresent()) {
            if (dishOwner.get() instanceof Meal) {
                Meal meal = (Meal) dishOwner.get();
                var day = repository.getDayByMealId(meal.getMealId());
                var plan = repository.getPlanByDayId(day.getDayId());
                List<Dish> dishes = updateDishes(updatedDish, meal::getDishes);
                meal.setDishes(dishes);
                plan.setLastUpdated(ZonedDateTime.now());
            } else if (dishOwner.get() instanceof PlanDay) {
                PlanDay day = (PlanDay) dishOwner.get();
                var plan = repository.getPlanByDayId(day.getDayId());
                List<Dish> dishes = updateDishes(updatedDish, day::getDishes);
                day.setDishes(dishes);
                plan.setLastUpdated(ZonedDateTime.now());
            }
        } else {
            dishDomainService.updateDish(updatedDish);
        }
    }

    private List<Dish> updateDishes(Dish dish, Supplier<List<Dish>> dishesSupplier) {
        List<Dish> dishes = dishesSupplier.get();
        for (int i = 0; i < dishes.size(); i++) {
            if (dishes.get(i).getDishId() == dish.getDishId()) {
                dishes.set(i, dish);
            }
        }
        return dishes;
    }

    /**
     * Removes selected {@link Dish}.
     *
     * @param id dish Id to delete
     */
    public void deleteDish(long id) {
        dishDomainService.deleteDish(id);
    }

    DishView mapDishView(Dish dish) {
        return DishView.builder()
                .id(dish.getDishId())
                .name(dish.getName())
                .categoryId(dish.getCategory().getCategoryId())
                .products(dish.getAllProducts().stream()
                        .map(pr -> {
                            //TODO mapping without reloading the product
                            final ProductView product = productService.getProduct(pr.getProductId());
                            product.setWeight(pr.getWeight());
                            return product;
                        })
                        .collect(Collectors.toList()))
                .calorific(dish.getCalorific())
                .proteins(dish.getCalorific())
                .fats(dish.getFats())
                .carbs(dish.getCarbs())
                .weight(dish.getWeight())
                .build();
    }

    private List<ProductRef> mapProductRefs(DishInfo dishInfo) {
        return dishInfo.getProducts().stream()
                .map(dp -> new ProductRef(
                        productService.getDomainProduct(dp.getProductId()),
                        Math.round(dp.getWeight() * 10)))
                .collect(Collectors.toList());
    }

    //TODO temporary methods to be refactored later
    public Dish getDishCopy(long id, long newId) {
        Dish domainDish = dishDomainService.getDish(id)
                .orElseThrow(() ->
                        new NotFoundException("Dish wasn't found"));
        return new Dish(newId, domainDish.getName(), domainDish.getDescription(), domainDish.getCategory(), domainDish.getProducts());
    }
}
