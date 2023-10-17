package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.model.dish.*;
import com.outdoor.foodcalc.model.product.ProductView;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DishServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private DishService dishService;

    @Mock
    private DishDomainService dishDomainService;

    @Mock
    private DishCategoryDomainService dishCategoryDomainService;

    @Mock
    private DishCategoryService dishCategories;

    @Mock
    private ProductService productService;

    private static final long CATEGORY_1_ID = 12345;
    private static final long CATEGORY_2_ID = 54321;
    private static final long CATEGORY_3_ID = 33321;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";
    private static final String CATEGORY_3_NAME = "Empty";
    private static final SimpleDishCategory SIMPLE_CAT_1 = new SimpleDishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final SimpleDishCategory SIMPLE_CAT_2 = new SimpleDishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
    private static final SimpleDishCategory SIMPLE_CAT_3 = new SimpleDishCategory(CATEGORY_3_ID, CATEGORY_3_NAME);
    private static final DishCategory DOMAIN_CAT_1 = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final DishCategory DOMAIN_CAT_2 = new DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
    private static final DishCategory DOMAIN_CAT_3 = new DishCategory(CATEGORY_3_ID, CATEGORY_3_NAME);

    private static final Product product1 = new Product(101010, "first prod",
            new ProductCategory(77777, "test product cat1"));
    private static final Product product2 = new Product(121212, "second prod", "",
            new ProductCategory(77777, "test product cat1"),
            1.1f, 3, 4.5f, 7, 110);
    private static final Product product3 = new Product(131313, "third prod", "",
            new ProductCategory(88888, "test product cat2"),
            13, 11.5f, 7, 32.2f, 55);

    private static final ProductRef productRef1 = new ProductRef(product1, 200);
    private static final ProductRef productRef2 = new ProductRef(product2, 300);
    private static final ProductRef productRef3 = new ProductRef(product3, 500);

    private static final ProductView productView1 = new ProductView(productRef1.getProductId(), productRef1.getName(),
            productRef1.getProductCategoryId(), productRef1.getCalorific(), productRef1.getProteins(), productRef1.getFats(),
            productRef1.getCarbs(), productRef1.getWeight());
    private static final ProductView productView2 = new ProductView(productRef2.getProductId(), productRef2.getName(),
            productRef2.getProductCategoryId(), productRef2.getCalorific(), productRef2.getProteins(), productRef2.getFats(),
            productRef2.getCarbs(), productRef2.getWeight());
    private static final ProductView productView3 = new ProductView(productRef3.getProductId(), productRef3.getName(),
            productRef3.getProductCategoryId(), productRef3.getCalorific(), productRef3.getProteins(), productRef3.getFats(),
            productRef3.getCarbs(), productRef3.getWeight());

    private static final long DISH_1_ID = 22222;
    private static final long DISH_2_ID = 33333;
    private static final long DISH_3_ID = 44444;


    private static final Dish domainDish1 = new Dish(DISH_1_ID, "domain dish1", "", DOMAIN_CAT_1,
            Arrays.asList(productRef1, productRef2, productRef3));
    private static final Dish domainDish2 = new Dish(DISH_2_ID, "domain dish2", "", DOMAIN_CAT_1,
            new ArrayList<>());
    private static final Dish domainDish3 = new Dish(DISH_3_ID, "domain dish3", "", DOMAIN_CAT_2,
            Arrays.asList(productRef1, productRef2, productRef3));

    private static final DishView dishView1 = new DishView(
            domainDish1.getDishId(), domainDish1.getName(), domainDish1.getCategory().getCategoryId(),
            6.83f, 6.65f, 0.0f, 18.2f, 100.0f,
            Arrays.asList(productView1, productView2, productView3));
    private static final DishView dishView2 = new DishView(
            domainDish2.getDishId(), domainDish2.getName(), domainDish2.getCategory().getCategoryId(),
            0f, 0, 0f, 0, 0f, new ArrayList<>());
    private static final DishView dishView3 = new DishView(
            domainDish3.getDishId(), domainDish3.getName(), domainDish3.getCategory().getCategoryId(),
            6.83f, 6.65f, 0.0f, 18.2f, 100.0f,
            Arrays.asList(productView1, productView2, productView3));

    private static final DishProduct dishProduct1 = new DishProduct(productRef1.getProductId(), productRef1.getWeight());
    private static final DishProduct dishProduct2 = new DishProduct(productRef2.getProductId(), productRef2.getWeight());
    private static final DishProduct dishProduct3 = new DishProduct(productRef3.getProductId(), productRef3.getWeight());


    @Test
    public void getAllDishesTest() {
        List<SimpleDishCategory> dishCategoryList = Arrays.asList(SIMPLE_CAT_1, SIMPLE_CAT_2, SIMPLE_CAT_3);
        List<Dish> domainDishList = Arrays.asList(domainDish1, domainDish2, domainDish3);

        when(dishCategories.getDishCategories()).thenReturn(dishCategoryList);
        when(dishDomainService.getAllDishes()).thenReturn(domainDishList);
        when(productService.getProduct(product1.getProductId())).thenReturn(productView1);
        when(productService.getProduct(product2.getProductId())).thenReturn(productView2);
        when(productService.getProduct(product3.getProductId())).thenReturn(productView3);

        CategoryWithDishes categoryWithDishes1 = new CategoryWithDishes(
                DOMAIN_CAT_1.getCategoryId(), DOMAIN_CAT_1.getName(), Arrays.asList(dishView1, dishView2));
        CategoryWithDishes categoryWithDishes2 = new CategoryWithDishes(
                DOMAIN_CAT_2.getCategoryId(), DOMAIN_CAT_2.getName(), List.of(dishView3));
        CategoryWithDishes categoryWithDishes3 = new CategoryWithDishes(
                DOMAIN_CAT_3.getCategoryId(), DOMAIN_CAT_3.getName(), new ArrayList<>());
        List<CategoryWithDishes> expected = Arrays.asList(categoryWithDishes1, categoryWithDishes2, categoryWithDishes3);

        List<CategoryWithDishes> actual = dishService.getAllDishes();
        assertEquals(expected, actual);

        verify(dishCategories).getDishCategories();
        verify(dishDomainService).getAllDishes();
        verify(productService, times(2)).getProduct(product1.getProductId());
        verify(productService, times(2)).getProduct(product2.getProductId());
        verify(productService, times(2)).getProduct(product3.getProductId());
    }

    @Test
    public void getDishTest() {
        Dish domainDish = domainDish1;
        DishView expectedDishView = dishView1;
        when(dishDomainService.getDish(domainDish.getDishId())).thenReturn(Optional.of(domainDish));
        when(productService.getProduct(product1.getProductId())).thenReturn(productView1);
        when(productService.getProduct(product2.getProductId())).thenReturn(productView2);
        when(productService.getProduct(product3.getProductId())).thenReturn(productView3);

        DishView actualDishView = dishService.getDish(domainDish.getDishId());
        assertEquals(expectedDishView, actualDishView);

        verify(dishDomainService).getDish(domainDish.getDishId());
        verify(productService).getProduct(product1.getProductId());
        verify(productService).getProduct(product2.getProductId());
        verify(productService).getProduct(product3.getProductId());
    }

    @Test(expected = NotFoundException.class)
    public void getDishNotFoundTest() {
        when(dishDomainService.getDish(DISH_1_ID)).thenReturn(Optional.empty());
        dishService.getDish(DISH_1_ID);
    }

    @Test
    public void addDishTest() {
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        Dish addedDomainDish = domainDish1;
        SimpleDish simpleDishToAdd = new SimpleDish(77777, domainDish1.getName(),
                domainDish1.getCategory().getCategoryId(), dishProductList);
        SimpleDish expectedSimpleDish = new SimpleDish(domainDish1.getDishId(), domainDish1.getName(),
                domainDish1.getCategory().getCategoryId(), dishProductList);

        when(dishCategoryDomainService.getCategory(simpleDishToAdd.getCategoryId()))
                .thenReturn(Optional.of(addedDomainDish.getCategory()));
        when(dishDomainService.addDish(any())).thenReturn(addedDomainDish);
        when(productService.getDomainProduct(dishProduct1.getProductId())).thenReturn(product1);
        when(productService.getDomainProduct(dishProduct2.getProductId())).thenReturn(product2);
        when(productService.getDomainProduct(dishProduct3.getProductId())).thenReturn(product3);

        SimpleDish actualSimpleDish = dishService.addDish(simpleDishToAdd);
        assertEquals(expectedSimpleDish, actualSimpleDish);

        verify(dishCategoryDomainService).getCategory(simpleDishToAdd.getCategoryId());
        verify(dishDomainService).addDish(any());
        verify(productService).getDomainProduct(dishProduct1.getProductId());
        verify(productService).getDomainProduct(dishProduct2.getProductId());
        verify(productService).getDomainProduct(dishProduct3.getProductId());
    }

    @Test(expected = NotFoundException.class)
    public void addDishCategoryFailTest() {
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        SimpleDish simpleDishToAdd = new SimpleDish(77777, domainDish1.getName(),
                domainDish1.getCategory().getCategoryId(), dishProductList);
        when(dishCategoryDomainService.getCategory(simpleDishToAdd.getCategoryId())).thenReturn(Optional.empty());

        dishService.addDish(simpleDishToAdd);
    }

    @Test
    public void updateDishTest() {
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        SimpleDish simpleDishToUpdate = new SimpleDish(domainDish1.getDishId(), domainDish1.getName(),
                domainDish1.getCategory().getCategoryId(), dishProductList);

        when(dishCategoryDomainService.getCategory(simpleDishToUpdate.getCategoryId()))
                .thenReturn(Optional.of(DOMAIN_CAT_1));
        when(productService.getDomainProduct(dishProduct1.getProductId())).thenReturn(product1);
        when(productService.getDomainProduct(dishProduct2.getProductId())).thenReturn(product2);
        when(productService.getDomainProduct(dishProduct3.getProductId())).thenReturn(product3);
        doNothing().when(dishDomainService).updateDish(any());

        dishService.updateDish(simpleDishToUpdate);
        verify(dishDomainService).updateDish(any());
    }

    @Test(expected = NotFoundException.class)
    public void updateDishCategoryFailTest() {
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        SimpleDish simpleDishToUpdate = new SimpleDish(domainDish1.getDishId(), domainDish1.getName(),
                domainDish1.getCategory().getCategoryId(), dishProductList);

        when(dishCategoryDomainService.getCategory(simpleDishToUpdate.getCategoryId())).thenReturn(Optional.empty());

        dishService.updateDish(simpleDishToUpdate);
    }

    @Test
    public void deleteDishTest() {
        doNothing().when(dishDomainService).deleteDish(DISH_1_ID);
        dishService.deleteDish(DISH_1_ID);
        verify(dishDomainService).deleteDish(DISH_1_ID);
    }
}
