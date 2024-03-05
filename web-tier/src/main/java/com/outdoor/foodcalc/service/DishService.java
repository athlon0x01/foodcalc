package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.FoodDetailsInstance;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.model.dish.CategoryWithDishes;
import com.outdoor.foodcalc.model.dish.DishCategoryView;
import com.outdoor.foodcalc.model.dish.DishInfo;
import com.outdoor.foodcalc.model.dish.DishView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DishService {
    private final DishDomainService dishDomainService;
    private final DishCategoryService dishCategoryService;
    private final ProductService productService;

    @Autowired
    public DishService(DishDomainService dishDomainService, DishCategoryService dishCategoryService, ProductService productService) {
        this.dishDomainService = dishDomainService;
        this.dishCategoryService = dishCategoryService;
        this.productService = productService;
    }

    /**
     * Gets all {@link Dish} objects mapped to {@link DishView}.
     *
     * @return list of products
     */
    public List<CategoryWithDishes> getAllTemplateDishes() {
        //load dishes & categories
        final List<DishCategoryView> categories = dishCategoryService.getDishCategories();
        final List<Dish> domainDishes = dishDomainService.getAllTemplateDishes();
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public DishInfo addDish(DishInfo dishInfo) {
        Dish dishToAdd = Dish.builder()
                .name(dishInfo.getName())
                .description(dishInfo.getDescription())
                .category(new DishCategory(dishInfo.getCategoryId(), ""))
                .template(true)
                .products(productService.buildMockProducts(dishInfo.getProducts()))
                .build();
        dishInfo.setId(dishDomainService.addDish(dishToAdd).getDishId());
        return dishInfo;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public DishView addMealDish(long planId, long dayId, long mealId, long id) {
        return mapDishView(dishDomainService.addMealDish(planId, dayId, mealId, id));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public DishView addDayDish(long planId, long dayId, long id) {
        return mapDishView(dishDomainService.addDayDish(planId, dayId, id));
    }

    /**
     * Updates selected {@link Dish} with new value.
     *
     * @param dishInfo updated
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateDish(DishInfo dishInfo) {
        Dish updatedDish = Dish.builder()
                .dishId(dishInfo.getId())
                .description(dishInfo.getDescription())
                .name(dishInfo.getName())
                .category(new DishCategory(dishInfo.getCategoryId(), ""))
                .products(productService.buildMockProducts(dishInfo.getProducts()))
                .build();
        dishDomainService.updateDish(updatedDish);
    }

    /**
     * Removes selected {@link Dish}.
     *
     * @param id dish Id to delete
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteDish(long id) {
        dishDomainService.deleteDish(id);
    }

    DishView mapDishView(Dish dish) {
        FoodDetailsInstance dishDetails = dish.getFoodDetails();
        return DishView.builder()
                .id(dish.getDishId())
                .name(dish.getName())
                .description(dish.getDescription())
                .categoryId(dish.getCategory().getCategoryId())
                .products(dish.getProducts().stream()
                        .map(productService::mapProductRef)
                        .collect(Collectors.toList()))
                .calorific(dishDetails.getCalorific())
                .proteins(dishDetails.getCalorific())
                .fats(dishDetails.getFats())
                .carbs(dishDetails.getCarbs())
                .weight(dishDetails.getWeight())
                .build();
    }

    List<Dish> buildMockDishes(List<Long> ids) {
        return ids.stream()
                .map(id -> Dish.builder().dishId(id).build())
                .collect(Collectors.toList());
    }
}
