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
    public List<Dish> allDishProducts() {
        List<Dish> dishes = dishRepo.getAllDishes();
        Map<Long, List<ProductRef>> allDishIdWithProductRefs = productRefRepo.getAllDishProducts();
        for (Dish dish : dishes) {
            List<ProductRef> products = allDishIdWithProductRefs.get(dish.getDishId());
            if(!products.isEmpty()) {
                dish.setProducts(products);
            }
        }
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
     * @return new {@link Dish} with auto generated id
     */
    public Dish addDish(Dish dish) {
        long id = dishRepo.addDish(dish);
        if(id == -1L) {
            throw new FoodcalcDomainException("Failed to add dish");
        }
        if((dish.getProducts().size() > 0) && (!productRefRepo.addDishProducts(dish))) {
            throw new FoodcalcDomainException("Fail to add products for dish with id=" + dish.getDishId());
        }
        return new Dish(id,
                dish.getName(),
                dish.getDescription(),
                dish.getCategory(),
                dish.getProducts());
    }

    /**
     * Update {@link Dish} object.
     *
     * @param dish to update
     */
    public void updateDish(Dish dish) {
        if(!dishRepo.existsDish(dish.getDishId())) {
            throw new NotFoundException("Dish with id=" + dish.getDishId() + " doesn't exist");
        }
        if(!productRefRepo.deleteDishProducts(dish.getDishId())) {
            throw new FoodcalcDomainException("Failed to delete products for dish with id=" + dish.getDishId());
        }
        if((dish.getProducts().size()>0) && (!productRefRepo.addDishProducts(dish))) {
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
     */
    public void deleteDish(long id) {
        if(!dishRepo.existsDish(id)) {
            throw new NotFoundException("Dish with id=" + id + " doesn't exist");
        }
        if(!productRefRepo.deleteDishProducts(id)) {
            throw new FoodcalcDomainException("Failed to delete products for dish with id=" + id);
        }
        if(!dishRepo.deleteDish(id)) {
            throw new FoodcalcDomainException("Failed to delete dish with id=" + id);
        }
    }
}
