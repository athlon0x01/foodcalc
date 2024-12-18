package com.outdoor.foodcalc.domain.service.dish;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.dish.IDishRepo;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRefRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link DishDomainService} class
 *
 * @author Olga Borovyk
 */
@ExtendWith(MockitoExtension.class)
public class DishDomainServiceTest {

    private static final Long DISH_ID = 12345L;
    private static final long CATEGORY_ID = 1111L;
    private static final DishCategory dummyDishCategory = new DishCategory(CATEGORY_ID, "first dishes");

    private static final ProductCategory dummyProductCategory =  new ProductCategory(
            2222L, "dummyCategory");

    private static final Product dummyProduct = Product.builder().productId(3333).name("dummyProduct")
            .category(dummyProductCategory).calorific(1.1f)
            .proteins(2.2f).fats(3.3f).carbs(4.4f).defaultWeight(10).build();

    private static final ProductRef dummyProductRef = new ProductRef(dummyProduct, 6666);

    private static final Dish dummyDish = new Dish(
            DISH_ID, "borsch", "dummyDescr",
            dummyDishCategory, true, Collections.emptyList());

    private static final Dish dummyDishWithProducts = new Dish(
            DISH_ID, "borsch", "dummyDescr",
            dummyDishCategory, true, Collections.singletonList(dummyProductRef));

    private static final Map<Long, List<ProductRef>> allDishesWithProducts = new HashMap<>();
    static {
        allDishesWithProducts.put(DISH_ID, Collections.singletonList(dummyProductRef));
    }


    @Mock
    private IDishRepo dishRepo;
    @Mock
    private IProductRefRepo productRefRepo;
    @Mock
    private IFoodPlanRepo foodPlanRepo;
    @Mock
    private DishCategoryDomainService dishCategoryService;

    @InjectMocks
    private DishDomainService service;

    @Test
    public void getAllDishesTest() {
        List<Dish> dishList = new ArrayList<>();
        List<Dish> expectedList = new ArrayList<>();
        Dish someDish = Dish.builder().dishId(11111L).name("some").category(dummyDishCategory).build();
        dishList.add(dummyDish);
        expectedList.add(dummyDishWithProducts);
        dishList.add(someDish);
        expectedList.add(someDish);

        when(dishRepo.getAllTemplateDishes()).thenReturn(dishList);
        when(productRefRepo.getAllTemplateDishesProducts()).thenReturn(allDishesWithProducts);

        List<Dish> actualDishList = service.getAllTemplateDishes();
        assertEquals(dishList.size(), actualDishList.size());
        assertEquals(dishList, actualDishList);
        assertEquals(expectedList.get(0).getProducts(), actualDishList.get(0).getProducts());
        assertEquals(expectedList.get(1).getProducts(), actualDishList.get(1).getProducts());


        verify(dishRepo).getAllTemplateDishes();
        verify(productRefRepo).getAllTemplateDishesProducts();
    }

    @Test
    public void getDishWithoutProductsTest() {
        Optional<Dish> expectedDish = Optional.of(dummyDish);
        when(dishRepo.getDish(DISH_ID)).thenReturn(expectedDish);
        when(productRefRepo.getDishProducts(DISH_ID)).thenReturn(Collections.emptyList());

        Optional<Dish> actualDish = service.getDish(DISH_ID);
        assertEquals(expectedDish, actualDish);

        verify(dishRepo).getDish(DISH_ID);
        verify(productRefRepo).getDishProducts(DISH_ID);
    }

    @Test
    public void getDishWithProductsTest() {
        Optional<Dish> dish = Optional.of(dummyDish);
        when(dishRepo.getDish(DISH_ID)).thenReturn(dish);
        List<ProductRef> productList = Collections.singletonList(dummyProductRef);
        when(productRefRepo.getDishProducts(DISH_ID)).thenReturn(productList);

        Optional<Dish> actualDish = service.getDish(DISH_ID);
        assertEquals(dummyDishWithProducts, actualDish.get());

        verify(dishRepo).getDish(DISH_ID);
        verify(productRefRepo).getDishProducts(DISH_ID);
    }

    @Test
    public void addDishWithoutProductsTest() {
        Dish dishToAdd = Dish.builder().dishId(-1).name("borsch").category(dummyDishCategory).template(true).build();
        Dish expectedDish = Dish.builder().dishId(DISH_ID).name("borsch").category(dummyDishCategory).template(true).build();
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(dishRepo.addDish(dishToAdd)).thenReturn(DISH_ID);
        when(productRefRepo.insertDishProducts(expectedDish)).thenReturn(true);

        Dish actualDish = service.addTemplateDish(dishToAdd);
        assertEquals(expectedDish, actualDish);

        verify(dishCategoryService).getCategory(CATEGORY_ID);
        verify(dishRepo).addDish(dishToAdd);
        verify(productRefRepo).insertDishProducts(expectedDish);
    }

    @Test
    public void addDishWithProductsTest() {
        Dish dishToAdd = new Dish(-1,"borsch", "dummyDescr",
                dummyDishCategory, true, Collections.singletonList(dummyProductRef));
        Dish addedDish = new Dish(DISH_ID,"borsch", "dummyDescr",
                dummyDishCategory, true, Collections.singletonList(dummyProductRef));
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(dishRepo.addDish(dishToAdd)).thenReturn(DISH_ID);
        when(productRefRepo.insertDishProducts(addedDish)).thenReturn(true);

        Dish actualDish = service.addTemplateDish(dishToAdd);
        assertEquals(dummyDishWithProducts, actualDish);

        verify(dishCategoryService).getCategory(CATEGORY_ID);
        verify(dishRepo).addDish(dishToAdd);
        verify(productRefRepo).insertDishProducts(addedDish);
    }

    @Test
    public void addDishFailTest() {
        Dish dishToAdd = Dish.builder().dishId(-1).name("borsch").category(dummyDishCategory).template(true).build();
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(dishRepo.addDish(dishToAdd)).thenReturn(-1L);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            service.addTemplateDish(dishToAdd);
        });
    }

    @Test
    public void addDishProductsFailTest() {
        Dish dishToAdd = new Dish(-1,"borsch", "dummyDescr",
                dummyDishCategory, true, Collections.singletonList(dummyProductRef));
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(dishRepo.addDish(dishToAdd)).thenReturn(DISH_ID);
        lenient().when(productRefRepo.insertDishProducts(dishToAdd)).thenReturn(false);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            service.addTemplateDish(dishToAdd);
        });
    }

    // dish with products : existsDish true, deleteDishProducts 1L, addDishProducts true, updateDish true
    // dish with products : existsDish false, deleteDishProducts never, addDishProducts never, updateDish never
    // dish with products : existsDish true, deleteDishProducts 1L, addDishProducts false, updateDish never
    // dish with products : existsDish true, deleteDishProducts 1L, addDishProducts true, updateDish false
    // dish without products : existsDish true, deleteDishProducts never, addDishProducts never, updateDish true
    @Test
    public void updateDishTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(productRefRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(1L);
        when(productRefRepo.insertDishProducts(dishToUpdate)).thenReturn(true);
        when(dishRepo.updateDish(dishToUpdate)).thenReturn(true);

        service.updateDish(7L, dishToUpdate);

        verify(dishRepo).existsDish(dishToUpdate.getDishId());
        verify(dishCategoryService).getCategory(CATEGORY_ID);
        verify(productRefRepo).deleteDishProducts(dishToUpdate.getDishId());
        verify(productRefRepo).insertDishProducts(dishToUpdate);
        verify(dishRepo).updateDish(dishToUpdate);
    }

    @Test
    public void updateDishWithoutProductsTest() {
        when(dishRepo.existsDish(dummyDish.getDishId())).thenReturn(true);
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(productRefRepo.deleteDishProducts(dummyDish.getDishId())).thenReturn(0L);
        when(productRefRepo.insertDishProducts(dummyDish)).thenReturn(true);
        when(dishRepo.updateDish(dummyDish)).thenReturn(true);

        service.updateDish(7L, dummyDish);

        verify(dishRepo).existsDish(dummyDish.getDishId());
        verify(dishCategoryService).getCategory(CATEGORY_ID);
        verify(productRefRepo).deleteDishProducts(dummyDish.getDishId());
        verify(productRefRepo).insertDishProducts(dummyDish);
        verify(dishRepo).updateDish(dummyDish);
    }

    @Test
    public void updateNotExistDishTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> {
            service.updateDish(7L, dishToUpdate);
        });
    }

    @Test
    public void updateDishAddProductsFailTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(productRefRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(1L);
        when(productRefRepo.insertDishProducts(dishToUpdate)).thenReturn(false);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            service.updateDish(7L, dishToUpdate);
        });
    }

    @Test
    public void updateDishFailTest() {
        Dish dishToUpdate = dummyDishWithProducts;
        when(dishRepo.existsDish(dishToUpdate.getDishId())).thenReturn(true);
        when(dishCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyDishCategory));
        when(productRefRepo.deleteDishProducts(dishToUpdate.getDishId())).thenReturn(1L);
        when(productRefRepo.insertDishProducts(dishToUpdate)).thenReturn(true);
        when(dishRepo.updateDish(dishToUpdate)).thenReturn(false);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            service.updateDish(7L, dishToUpdate);
        });
    }

    @Test
    public void deleteDishTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(true);
        when(productRefRepo.deleteDishProducts(DISH_ID)).thenReturn(1L);
        when(dishRepo.deleteDish(DISH_ID)).thenReturn(true);

        service.deleteTemplateDish(DISH_ID);

        verify(dishRepo).existsDish(DISH_ID);
        verify(productRefRepo).deleteDishProducts(DISH_ID);
        verify(dishRepo).deleteDish(DISH_ID);
    }

    @Test
    public void deleteDishWithoutProductsTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(true);
        when(productRefRepo.deleteDishProducts(DISH_ID)).thenReturn(0L);
        when(dishRepo.deleteDish(DISH_ID)).thenReturn(true);

        service.deleteTemplateDish(DISH_ID);

        verify(dishRepo).existsDish(DISH_ID);
        verify(productRefRepo).deleteDishProducts(DISH_ID);
        verify(dishRepo).deleteDish(DISH_ID);
    }

    @Test
    public void deleteNotExistDishTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> {
            service.deleteTemplateDish(DISH_ID);
        });
    }

    @Test
    public void deleteDishFailTest() {
        when(dishRepo.existsDish(DISH_ID)).thenReturn(true);
        when(productRefRepo.deleteDishProducts(DISH_ID)).thenReturn(1L);
        when(dishRepo.deleteDish(DISH_ID)).thenReturn(false);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            service.deleteTemplateDish(DISH_ID);
        });
    }
}
