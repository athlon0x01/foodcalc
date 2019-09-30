package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.endpoint.impl.DishCategoryEndpoint;
import com.outdoor.foodcalc.model.dish.*;
import com.outdoor.foodcalc.model.product.ProductView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DishService {

    private DishCategoryEndpoint dishCategories;
    private ProductService productService;

    private final List<Dish> dishes = new ArrayList<>();

    @Autowired
    public DishService(DishCategoryEndpoint dishCategories, ProductService productService) {
        this.dishCategories = dishCategories;
        this.productService = productService;
    }

    public List<CategoryWithDishes> getAllDishes() {
        //load dishes & categories
        final List<SimpleDishCategory> categories = dishCategories.getDishCategories();
        //group dishes by categories
        final Map<Long, List<Dish>> dishesMap = dishes.stream()
                .collect(Collectors.groupingBy(d -> d.getCategory().getCategoryId()));
        //map domain classes to UI model
        return categories.stream()
                .map(c -> {
                    final CategoryWithDishes cView = new CategoryWithDishes();
                    cView.id = c.id;
                    cView.name = c.name;
                    final List<Dish> dishList = dishesMap.get(c.id);
                    cView.dishes = (dishList == null) ? new ArrayList<>() : dishList.stream()
                            .map(this::mapDishView)
                            .collect(Collectors.toList());
                    return cView;
                })
                .collect(Collectors.toList());
    }

    public DishView getDish(long id) {
        final Optional<DishView> first = dishes.stream()
                .filter(dish -> dish.getDishId() == id)
                .map(this::mapDishView)
                .findFirst();
        return first.orElseThrow(() -> new NotFoundException(String.valueOf(id)));
    }

    public SimpleDish addDish(SimpleDish dish) {
        dish.id = dishes.stream()
                .map(Dish::getDishId)
                .max(Long::compareTo)
                .orElse((long) dishes.size())
                + 1;
        Dish domainDish = new Dish(dish.id, dish.name, mapDishCategory(dish.categoryId));
        domainDish.setProducts(mapProductRefs(dish));
        dishes.add(domainDish);
        return dish;
    }

    public boolean updateDish(SimpleDish dish) {
        final Optional<Dish> first = dishes.stream()
                .filter(p -> p.getDishId() == dish.id)
                .findFirst();
        Dish original = first.orElseThrow(() -> new NotFoundException(String.valueOf(dish.id)));
        if (original.getCategory().getCategoryId() != dish.categoryId) {
            final DishCategory category = mapDishCategory(dish.categoryId);
            original.setCategory(category);
        }
        original.setName(dish.name);
        original.setProducts(mapProductRefs(dish));
        return true;
    }

    public boolean deleteDish(long id) {
        int index = 0;
        while (index < dishes.size()) {
            if (dishes.get(index).getDishId() == id) {
                dishes.remove(index);
                return true;
            }
            index++;
        }
        return false;
    }

    private DishView mapDishView(Dish dish) {
        DishView view = new DishView();
        view.id = dish.getDishId();
        view.name = dish.getName();
        view.categoryId = dish.getCategory().getCategoryId();
        view.products = dish.getAllProducts().stream()
                .map(pr -> {
                    final ProductView product = productService.getProduct(pr.getProductId());
                    product.weight = pr.getWeight();
                    return product;
                })
                .collect(Collectors.toList());
        return view;
    }

    private DishCategory mapDishCategory(long id) {
        final SimpleDishCategory category = dishCategories.getDishCategory(id);
        if (category == null) {
            throw  new NotFoundException("Failed to get Dish Category, id = " + id);
        }
        return new DishCategory(category.id, category.name);
    }

    private List<ProductRef> mapProductRefs(SimpleDish dish) {
        return dish.products.stream()
                .map(dp -> new ProductRef(
                        productService.getDomainProduct(dp.productId),
                        Math.round(dp.weight * 10)))
                .collect(Collectors.toList());
    }
}
