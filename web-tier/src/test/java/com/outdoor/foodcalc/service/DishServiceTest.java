package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.model.dish.*;
import com.outdoor.foodcalc.model.product.SimpleProduct;
import com.outdoor.foodcalc.model.product.ProductView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DishServiceTest {

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

    @Mock
    private FoodPlansRepo repo;

    private static final long CATEGORY_1_ID = 12345;
    private static final long CATEGORY_2_ID = 54321;
    private static final long CATEGORY_3_ID = 33321;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";
    private static final String CATEGORY_3_NAME = "Empty";
    private static final DishCategory SIMPLE_CAT_1 = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final DishCategory SIMPLE_CAT_2 = new DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
    private static final DishCategory SIMPLE_CAT_3 = new DishCategory(CATEGORY_3_ID, CATEGORY_3_NAME);
    private static final com.outdoor.foodcalc.domain.model.dish.DishCategory DOMAIN_CAT_1 = new com.outdoor.foodcalc.domain.model.dish.DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final com.outdoor.foodcalc.domain.model.dish.DishCategory DOMAIN_CAT_2 = new com.outdoor.foodcalc.domain.model.dish.DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
    private static final com.outdoor.foodcalc.domain.model.dish.DishCategory DOMAIN_CAT_3 = new com.outdoor.foodcalc.domain.model.dish.DishCategory(CATEGORY_3_ID, CATEGORY_3_NAME);

    private static final Product product1 = Product.builder().productId(101010).name("first prod")
            .category(new ProductCategory(77777, "test product cat1")).build();
    private static final Product product2 = Product.builder().productId(121212).name("second prod").description("")
            .category(new ProductCategory(77777, "test product cat1")).calorific(1.1f)
            .proteins(3).fats(4.5f).carbs(7).defaultWeight(100).build();
    private static final Product product3 = Product.builder().productId(131313).name("third prod").description("")
            .category(new ProductCategory(88888, "test product cat2")).calorific(13)
            .proteins(11.5f).fats(7).carbs(32.2f).defaultWeight(100).build();

    private static final ProductRef productRef1 = new ProductRef(product1, 200);
    private static final ProductRef productRef2 = new ProductRef(product2, 300);
    private static final ProductRef productRef3 = new ProductRef(product3, 500);

    private static final ProductView productView1 = ProductView.builder()
            .id(productRef1.getProductId()).name(productRef1.getName())
            .categoryId(productRef1.getProductCategoryId()).calorific(productRef1.getCalorific())
            .proteins(productRef1.getProteins()).fats(productRef1.getFats())
            .carbs(productRef1.getCarbs()).weight(productRef1.getWeight()).build();
    private static final ProductView productView2 = ProductView.builder()
            .id(productRef2.getProductId()).name(productRef2.getName())
            .categoryId(productRef2.getProductCategoryId()).calorific(productRef2.getCalorific())
            .proteins(productRef2.getProteins()).fats(productRef2.getFats())
            .carbs(productRef2.getCarbs()).weight(productRef2.getWeight()).build();
    private static final ProductView productView3 = ProductView.builder()
            .id(productRef3.getProductId()).name(productRef3.getName())
            .categoryId(productRef3.getProductCategoryId()).calorific(productRef3.getCalorific())
            .proteins(productRef3.getProteins()).fats(productRef3.getFats())
            .carbs(productRef3.getCarbs()).weight(productRef3.getWeight()).build();

    private static final long DISH_1_ID = 22222;
    private static final long DISH_2_ID = 33333;
    private static final long DISH_3_ID = 44444;


    private static final Dish domainDish1 = new Dish(DISH_1_ID, "domain dish1", "", DOMAIN_CAT_1,
            Arrays.asList(productRef1, productRef2, productRef3));
    private static final Dish domainDish2 = new Dish(DISH_2_ID, "domain dish2", "", DOMAIN_CAT_1,
            new ArrayList<>());
    private static final Dish domainDish3 = new Dish(DISH_3_ID, "domain dish3", "", DOMAIN_CAT_2,
            Arrays.asList(productRef1, productRef2, productRef3));

    private static final DishView dishView1 = DishView.builder().id(domainDish1.getDishId())
            .name(domainDish1.getName()).categoryId(domainDish1.getCategory().getCategoryId())
            .calorific(6.83f).proteins(6.83f).fats(4.85f).carbs(18.2f).weight(100.0f)
            .products(Arrays.asList(productView1, productView2, productView3)).build();
    private static final DishView dishView2 = DishView.builder().id(domainDish2.getDishId())
            .name(domainDish2.getName()).categoryId(domainDish2.getCategory().getCategoryId())
            .calorific(0f).proteins(0f).fats(0f).carbs(0f).weight(0f)
            .products(new ArrayList<>()).build();
    private static final DishView dishView3 = DishView.builder().id(domainDish3.getDishId())
        .name(domainDish3.getName()).categoryId(domainDish3.getCategory().getCategoryId())
            .calorific(6.83f).proteins(6.83f).fats(4.85f).carbs(18.2f).weight(100.0f)
            .products(Arrays.asList(productView1, productView2, productView3)).build();

    private static final SimpleProduct dishProduct1 = SimpleProduct.builder()
            .productId(productRef1.getProductId()).weight(productRef1.getWeight()).build();
    private static final SimpleProduct dishProduct2 = SimpleProduct.builder()
            .productId(productRef2.getProductId()).weight(productRef2.getWeight()).build();

    private static final SimpleProduct dishProduct3 = SimpleProduct.builder()
            .productId(productRef3.getProductId()).weight(productRef3.getWeight()).build();

    @Test
    public void getAllDishesTest() {
        List<DishCategory> dishCategoryList = Arrays.asList(SIMPLE_CAT_1, SIMPLE_CAT_2, SIMPLE_CAT_3);
        List<Dish> domainDishList = Arrays.asList(domainDish1, domainDish2, domainDish3);

        when(dishCategories.getDishCategories()).thenReturn(dishCategoryList);
        when(dishDomainService.getAllDishes()).thenReturn(domainDishList);
        when(productService.getProduct(product1.getProductId())).thenReturn(productView1);
        when(productService.getProduct(product2.getProductId())).thenReturn(productView2);
        when(productService.getProduct(product3.getProductId())).thenReturn(productView3);

        CategoryWithDishes categoryWithDishes1 = CategoryWithDishes.builder()
                .id(DOMAIN_CAT_1.getCategoryId()).name(DOMAIN_CAT_1.getName())
                .dishes(Arrays.asList(dishView1, dishView2)).build();
        CategoryWithDishes categoryWithDishes2 = CategoryWithDishes.builder()
                .id(DOMAIN_CAT_2.getCategoryId()).name(DOMAIN_CAT_2.getName())
                .dishes(List.of(dishView3)).build();
        CategoryWithDishes categoryWithDishes3 = CategoryWithDishes.builder()
                .id(DOMAIN_CAT_3.getCategoryId()).name(DOMAIN_CAT_3.getName())
                .dishes(new ArrayList<>()).build();
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

    @Test
    public void getDishNotFoundTest() {
        when(dishDomainService.getDish(DISH_1_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            dishService.getDish(DISH_1_ID);
        });
    }

    @Test
    public void addDishTest() {
        List<SimpleProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        Dish addedDomainDish = domainDish1;
        DishInfo dishInfoToAdd = DishInfo.builder().id(77777).name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();
        DishInfo expectedDishInfo = DishInfo.builder().id(domainDish1.getDishId()).name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();

        when(dishCategoryDomainService.getCategory(dishInfoToAdd.getCategoryId()))
                .thenReturn(Optional.of(addedDomainDish.getCategory()));
        when(dishDomainService.addDish(any())).thenReturn(addedDomainDish);
        when(productService.getDomainProduct(dishProduct1.getProductId())).thenReturn(product1);
        when(productService.getDomainProduct(dishProduct2.getProductId())).thenReturn(product2);
        when(productService.getDomainProduct(dishProduct3.getProductId())).thenReturn(product3);

        DishInfo actualDishInfo = dishService.addDish(dishInfoToAdd);
        assertEquals(expectedDishInfo, actualDishInfo);

        verify(dishCategoryDomainService).getCategory(dishInfoToAdd.getCategoryId());
        verify(dishDomainService).addDish(any());
        verify(productService).getDomainProduct(dishProduct1.getProductId());
        verify(productService).getDomainProduct(dishProduct2.getProductId());
        verify(productService).getDomainProduct(dishProduct3.getProductId());
    }

    @Test
    public void addDishCategoryFailTest() {
        List<SimpleProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        DishInfo dishInfoToAdd = DishInfo.builder().id(77777).name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();
        when(dishCategoryDomainService.getCategory(dishInfoToAdd.getCategoryId())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            dishService.addDish(dishInfoToAdd);
        });
    }

    @Test
    public void updateDishTest() {
        List<SimpleProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        DishInfo dishInfoToUpdate = DishInfo.builder().id(domainDish1.getDishId()).name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();

        when(repo.getDishOwner(domainDish1.getDishId())).thenReturn(Optional.empty());
        when(dishCategoryDomainService.getCategory(dishInfoToUpdate.getCategoryId()))
                .thenReturn(Optional.of(DOMAIN_CAT_1));
        when(productService.getDomainProduct(dishProduct1.getProductId())).thenReturn(product1);
        when(productService.getDomainProduct(dishProduct2.getProductId())).thenReturn(product2);
        when(productService.getDomainProduct(dishProduct3.getProductId())).thenReturn(product3);
        doNothing().when(dishDomainService).updateDish(any());

        dishService.updateDish(dishInfoToUpdate);
        verify(dishDomainService).updateDish(any());
    }

    @Test
    public void updateDishCategoryFailTest() {
        List<SimpleProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        DishInfo dishInfoToUpdate = DishInfo.builder().id(domainDish1.getDishId()).name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();

        when(dishCategoryDomainService.getCategory(dishInfoToUpdate.getCategoryId())).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            dishService.updateDish(dishInfoToUpdate);
        });
    }

    @Test
    public void deleteDishTest() {
        doNothing().when(dishDomainService).deleteDish(DISH_1_ID);
        dishService.deleteDish(DISH_1_ID);
        verify(dishDomainService).deleteDish(DISH_1_ID);
    }
}
