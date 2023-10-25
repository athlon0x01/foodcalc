package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.model.dish.*;
import com.outdoor.foodcalc.model.product.ProductView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DishService {
    private final DishDomainService dishDomainService;
    private final DishCategoryDomainService dishCategoryDomainService;
    private final DishCategoryService dishCategoryService;
    private final ProductService productService;

    @Autowired
    public DishService(DishDomainService dishDomainService, DishCategoryDomainService dishCategoryDomainService, DishCategoryService dishCategoryService, ProductService productService) {
        this.dishDomainService = dishDomainService;
        this.dishCategoryDomainService = dishCategoryDomainService;
        this.dishCategoryService = dishCategoryService;
        this.productService = productService;
    }

    /**
     * Gets all {@link Dish} objects mapped to {@link DishView}.
     *
     * @return list of products
     */
    public List<CategoryWithDishes> getAllDishes() {
        //load dishes & categories
        final List<SimpleDishCategory> categories = dishCategoryService.getDishCategories();
        final List<Dish> domainDishes = dishDomainService.getAllDishes();
        //group dishes by categories
        final Map<Long, List<Dish>> dishesMap = domainDishes.stream()
                .collect(Collectors.groupingBy(d -> d.getCategory().getCategoryId()));
        //map domain classes to UI model
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
     * @param simpleDish dish to add
     * @return new dish
     */
    public SimpleDish addDish(SimpleDish simpleDish) {
        final DishCategory category = dishCategoryDomainService.getCategory(simpleDish.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Dish Category, id = " + simpleDish.getCategoryId()));

        Dish dishToAdd = new Dish( -1, simpleDish.getName(), "", category, mapProductRefs(simpleDish));
        simpleDish.setId(dishDomainService.addDish(dishToAdd).getDishId());
        return simpleDish;
    }

    /**
     * Updates selected {@link Dish} with new value.
     *
     * @param simpleDish updated
     */
    public void updateDish(SimpleDish simpleDish) {
        final DishCategory category = dishCategoryDomainService.getCategory(simpleDish.getCategoryId())
                .orElseThrow(() ->
                        new NotFoundException("Failed to get Dish Category, id = " + simpleDish.getCategoryId() ));

        Dish updatedDish = new Dish(simpleDish.getId(), simpleDish.getName(), "", category, mapProductRefs(simpleDish));
        dishDomainService.updateDish(updatedDish);
    }

    /**
     * Removes selected {@link Dish}.
     *
     * @param id dish Id to delete
     */
    public void deleteDish(long id) {
        dishDomainService.deleteDish(id);
    }

    private DishView mapDishView(Dish dish) {
        return DishView.builder()
                .id(dish.getDishId())
                .name(dish.getName())
                .categoryId(dish.getCategory().getCategoryId())
                .products(dish.getAllProducts().stream()
                        .map(pr -> {
                            final ProductView product = productService.getProduct(pr.getProductId());
                            product.setWeight(pr.getWeight());
                            return product;
                        })
                        .collect(Collectors.toList()))
                .calorific(dish.getCalorific())
                .proteins(dish.getCalorific())
                .carbs(dish.getCarbs())
                .weight(dish.getWeight())
                .build();
    }

    private List<ProductRef> mapProductRefs(SimpleDish simpleDish) {
        return simpleDish.getProducts().stream()
                .map(dp -> new ProductRef(
                        productService.getDomainProduct(dp.getProductId()),
                        Math.round(dp.getWeight() * 10)))
                .collect(Collectors.toList());
    }
}
