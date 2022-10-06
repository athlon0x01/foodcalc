package com.outdoor.foodcalc.domain.service.dish;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.dish.IDishRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link DishDomainService} class
 *
 * @author Olga Borovyk
 */
public class DishDomainServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final Long DISH_ID = 12345L;

    private static final DishCategory dummyDishCategory = new DishCategory(1111L, "first dishes");

    private static final ProductCategory dummyProductCategory =  new ProductCategory(
            2222L, "dummyCategory");

    private static final Product dummyProduct = new Product(
            3333, "dummyProduct", "dummyDescr", dummyProductCategory,
            1.1f, 2.2f, 3.3f, 4.4f, 10);

    private static final ProductRef dummyProductRef = new ProductRef(dummyProduct, 6666);

    private static final Dish dummyDish = new Dish(
            DISH_ID, "borsch", "dummyDescr",
            dummyDishCategory, Collections.EMPTY_LIST);

    private static final Dish dummyDishWithProducts = new Dish(
            DISH_ID, "borsch", "dummyDescr",
            dummyDishCategory, Collections.singletonList(dummyProductRef));

    @Mock
    private IDishRepo dishRepo;

    @Mock
    private IProductRepo productRepo;

    @InjectMocks
    private DishDomainService service;

    @Test
    public void getAllDishesTest() {
        List<Dish> dishList = Collections.singletonList(dummyDish);
        List<ProductRef> productList = Collections.singletonList(dummyProductRef);
        List<Dish> expectedDishList = Collections.singletonList(dummyDishWithProducts);

        when(dishRepo.getAllDishes()).thenReturn(dishList);
        when(productRepo.getDishProducts(dummyDish.getDishId())).thenReturn(productList);

        List<Dish> actualDishList = service.getAllDishes();
        assertEquals(actualDishList.size(), expectedDishList.size());
        assertEquals(expectedDishList, actualDishList);
        assertEquals(expectedDishList.get(0), actualDishList.get(0));

        verify(dishRepo).getAllDishes();
        verify(productRepo).getDishProducts(DISH_ID);
    }

    @Test
    public void getDishWithoutProductsTest() {
        Optional<Dish> expectedDish = Optional.empty();
        when(dishRepo.getDish(DISH_ID)).thenReturn(expectedDish);

        Optional<Dish> actualDish = service.getDish(DISH_ID);
        assertEquals(expectedDish, actualDish);

        verify(dishRepo).getDish(DISH_ID);
        verify(productRepo, never()).getDishProducts(anyLong());
    }

    @Test
    public void getDishWithProductsTest() {
        Optional<Dish> dish = Optional.of(dummyDish);
        when(dishRepo.getDish(DISH_ID)).thenReturn(dish);
        List<ProductRef> productList = Collections.singletonList(dummyProductRef);
        when(productRepo.getDishProducts(DISH_ID)).thenReturn(productList);

        Optional<Dish> actualDish = service.getDish(DISH_ID);
        assertEquals(dummyDishWithProducts, actualDish.get());

        verify(dishRepo).getDish(DISH_ID);
        verify(productRepo).getDishProducts(DISH_ID);
    }

    @Test
    public void addDishWithoutProductsTest() {
        Dish dishToAdd = new Dish(-1,"borsch", dummyDishCategory);
        Dish expectedDish = new Dish(DISH_ID,"borsch", dummyDishCategory);
        when(dishRepo.addDish(dishToAdd)).thenReturn(DISH_ID);

        Dish actualDish = service.addDish(dishToAdd);
        assertEquals(expectedDish, actualDish);

        verify(dishRepo).addDish(dishToAdd);
        verify(productRepo, never()).addDishProducts(dishToAdd.getDishId(), dishToAdd.getProducts());
    }

    @Test
    public void addDishWithProductsTest() {
        Dish dishToAdd = new Dish(-1,"borsch", "dummyDescr",
                dummyDishCategory, Collections.singletonList(dummyProductRef));
        when(dishRepo.addDish(dishToAdd)).thenReturn(DISH_ID);
        when(productRepo.addDishProducts(dishToAdd.getDishId(), dishToAdd.getProducts())).thenReturn(true);

        Dish actualDish = service.addDish(dishToAdd);
        assertEquals(dummyDishWithProducts, actualDish);

        verify(dishRepo).addDish(dishToAdd);
        verify(productRepo).addDishProducts(dishToAdd.getDishId(), dishToAdd.getProducts());
    }

    @Test(expected = FoodcalcDomainException.class)
    public void addDishFailTest() {
        Dish dishToAdd = new Dish(-1,"borsch", dummyDishCategory);
        when(dishRepo.addDish(dishToAdd)).thenReturn(-1L);

        service.addDish(dishToAdd);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void addDishProductsFailTest() {
        Dish dishToAdd = new Dish(-1,"borsch", "dummyDescr",
                dummyDishCategory, Collections.singletonList(dummyProductRef));
        when(dishRepo.addDish(dishToAdd)).thenReturn(DISH_ID);
        when(productRepo.addDishProducts(dishToAdd.getDishId(), dishToAdd.getProducts())).thenReturn(false);

        service.addDish(dishToAdd);
    }

    // dish with products : existsDish true, deleteDishProducts true, addDishProducts true, updateDish true
    // dish with products : existsDish false, deleteDishProducts never, addDishProducts never, updateDish never
    // dish with products : existsDish true, deleteDishProducts false, addDishProducts never, updateDish never
    // dish with products : existsDish true, deleteDishProducts true, addDishProducts false, updateDish never
    // dish with products : existsDish true, deleteDishProducts true, addDishProducts true, updateDish false
    // dish without products : existsDish true, deleteDishProducts true, addDishProducts never, updateDish true
    @Test
    public void updateDishTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.addDishProducts(dishToUpdate.getDishId(), dishToUpdate.getProducts())).thenReturn(true);
        when(dishRepo.updateDish(dishToUpdate)).thenReturn(true);

        service.updateDish(dishToUpdate);

        verify(dishRepo).existsDish(dishToUpdate.getDishId());
        verify(productRepo).deleteDishProducts(dishToUpdate.getDishId());
        verify(productRepo).addDishProducts(dishToUpdate.getDishId(), dishToUpdate.getProducts());
        verify(dishRepo).updateDish(dishToUpdate);
    }

    @Test
    public void updateDishWithoutProductsTest() {
        Dish dishToUpdate = dummyDish;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(true);
        when(dishRepo.updateDish(dishToUpdate)).thenReturn(true);

        service.updateDish(dishToUpdate);

        verify(dishRepo).existsDish(dishToUpdate.getDishId());
        verify(productRepo).deleteDishProducts(dishToUpdate.getDishId());
        verify(productRepo, never()).addDishProducts(dishToUpdate.getDishId(), dishToUpdate.getProducts());
        verify(dishRepo).updateDish(dishToUpdate);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExistDishTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(false);

        service.updateDish(dishToUpdate);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void updateDishDeleteProductsFailTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(false);

        service.updateDish(dishToUpdate);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void updateDishAddProductsFailTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.addDishProducts(dishToUpdate.getDishId(), dishToUpdate.getProducts())).thenReturn(false);

        service.updateDish(dishToUpdate);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void updateDishFailTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(true);
        when(productRepo.addDishProducts(dishToUpdate.getDishId(), dishToUpdate.getProducts())).thenReturn(true);
        when(dishRepo.updateDish(dishToUpdate)).thenReturn(false);

        service.updateDish(dishToUpdate);
    }

    @Test
    public void deleteDishTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(true);
        when(productRepo.deleteDishProducts(DISH_ID)).thenReturn(true);
        when(dishRepo.deleteDish(DISH_ID)).thenReturn(true);

        service.deleteDish(DISH_ID);

        verify(dishRepo).existsDish(DISH_ID);
        verify(productRepo).deleteDishProducts(DISH_ID);
        verify(dishRepo).deleteDish(DISH_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExistDishTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(false);

        service.deleteDish(DISH_ID);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void deleteDishDeleteProductsFailTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(true);
        when(productRepo.deleteDishProducts(DISH_ID)).thenReturn(false);

        service.deleteDish(DISH_ID);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void deleteDishFailTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(true);
        when(productRepo.deleteDishProducts(DISH_ID)).thenReturn(true);
        when(dishRepo.deleteDish(DISH_ID)).thenReturn(false);

        service.deleteDish(DISH_ID);
    }
}
