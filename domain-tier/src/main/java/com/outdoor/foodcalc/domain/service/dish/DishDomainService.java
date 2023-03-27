package com.outdoor.foodcalc.domain.service.dish;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.dish.IDishRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRefRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Domain service for all operations with {@link Dish} objects.
 *
 * @author Olga Borovyk
 */
@Service
public class DishDomainService {


    private final IDishRepo dishRepo;

    private final IProductRefRepo productRefRepo;

    @Autowired
    public DishDomainService(IDishRepo dishRepo, IProductRefRepo productRefRepo) {
        this.dishRepo = dishRepo;
        this.productRefRepo = productRefRepo;
    }

    /**
     * Loads all {@link Dish} objects.
     *
     * @return list of dishes
     */
    public List<Dish> getAllDishes() {
        List<Dish> dishes = dishRepo.getAllDishes();
        Map<Long, List<ProductRef>> allDishIdWithProductRefs = productRefRepo.getAllDishProducts();

        dishes.stream()
                .filter(dish -> allDishIdWithProductRefs.containsKey(dish.getDishId()))
                .forEach(dish -> dish.setProducts(allDishIdWithProductRefs.get(dish.getDishId())));
        return dishes;
    }

    /**
     * Loads {@link Dish} object.
     *
     * @param id dish id
     * @return loaded dish
     */
    public Optional<Dish> getDish(long id) {
        Optional<Dish> dish = dishRepo.getDish(id);
        dish.ifPresent(value -> {
            List<ProductRef> dishProducts = productRefRepo.getDishProducts(value.getDishId());
            if(!dishProducts.isEmpty()) {
                value.setProducts(dishProducts);
            }
        });
        return dish;
    }

    /**
     * Add new {@link Dish} object.
     *
     * @param dish to add
     *
     * @throws FoodcalcDomainException If dish wasn't added
     * @throws FoodcalcDomainException If dish products weren't added
     * @return new {@link Dish} with auto generated id
     */
    public Dish addDish(Dish dish) {
        long id = dishRepo.addDish(dish);
        if(id == -1L) {
            throw new FoodcalcDomainException("Failed to add dish");
        }
        Dish addedDish = new Dish(id,
                dish.getName(),
                dish.getDescription(),
                dish.getCategory(),
                dish.getProducts());
        if((!productRefRepo.addDishProducts(addedDish)) && (addedDish.getProducts().size() > 0)) {
            throw new FoodcalcDomainException("Fail to add products for dish with id=" + addedDish.getDishId());
        }
        return addedDish;
    }

    /**
     * Update {@link Dish} object.
     *
     * @param dish to update
     *
     * @throws NotFoundException If dish doesn't exist
     * @throws FoodcalcDomainException If dish products weren't added
     * @throws FoodcalcDomainException If dish wasn't updated
     */
    public void updateDish(Dish dish) {
        if(!dishRepo.existsDish(dish.getDishId())) {
            throw new NotFoundException("Dish with id=" + dish.getDishId() + " doesn't exist");
        }
        productRefRepo.deleteDishProducts(dish.getDishId());
        if((!dish.getProducts().isEmpty()) && (!productRefRepo.addDishProducts(dish))) {
            throw new FoodcalcDomainException("Failed to add products for dish with id=" + dish.getDishId());
        }

        if(!dishRepo.updateDish(dish)) {
            throw new FoodcalcDomainException("Failed to update dish with id=" + dish.getDishId());
        }
    }

    /**
     * Delete {@link Dish} object with all related {@link ProductRef}.
     *
     * @param id dish to delete
     *
     * @throws NotFoundException If dish doesn't exist
     * @throws FoodcalcDomainException If dish wasn't deleted
     */
    public void deleteDish(long id) {
        if(!dishRepo.existsDish(id)) {
            throw new NotFoundException("Dish with id=" + id + " doesn't exist");
        }
        productRefRepo.deleteDishProducts(id);
        if(!dishRepo.deleteDish(id)) {
            throw new FoodcalcDomainException("Failed to delete dish with id=" + id);
        }
    }
}
