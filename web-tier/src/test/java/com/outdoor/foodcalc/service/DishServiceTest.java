package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.service.dish.DishDomainService;
import com.outdoor.foodcalc.model.dish.CategoryWithDishes;
import com.outdoor.foodcalc.model.dish.DishCategoryView;
import com.outdoor.foodcalc.model.dish.DishInfo;
import com.outdoor.foodcalc.model.dish.DishView;
import com.outdoor.foodcalc.model.product.ProductItem;
import com.outdoor.foodcalc.model.product.ProductView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DishServiceTest {

    @InjectMocks
    private DishService dishService;

    @Mock
    private DishDomainService dishDomainService;

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
    private static final DishCategoryView SIMPLE_CAT_1 = DishCategoryView.builder().id(CATEGORY_1_ID).name(CATEGORY_1_NAME).build();
    private static final DishCategoryView SIMPLE_CAT_2 = DishCategoryView.builder().id(CATEGORY_2_ID).name(CATEGORY_2_NAME).build();
    private static final DishCategoryView SIMPLE_CAT_3 = DishCategoryView.builder().id(CATEGORY_3_ID).name(CATEGORY_3_NAME).build();
    private static final DishCategory DOMAIN_CAT_1 = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final DishCategory DOMAIN_CAT_2 = new DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
    private static final DishCategory DOMAIN_CAT_3 = new DishCategory(CATEGORY_3_ID, CATEGORY_3_NAME);

    private static final Product product1 = Product.builder().productId(101010).name("first prod")
            .category(new ProductCategory(77777, "test product cat1")).build();
    private static final Product product2 = Product.builder().productId(121212).name("second prod")
            .category(new ProductCategory(77777, "test product cat1")).calorific(1.1f)
            .proteins(3).fats(4.5f).carbs(7).defaultWeight(100).build();
    private static final Product product3 = Product.builder().productId(131313).name("third prod")
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
            true, Arrays.asList(productRef1, productRef2, productRef3));
    private static final Dish domainDish2 = new Dish(DISH_2_ID, "domain dish2", "", DOMAIN_CAT_1,
            true, new ArrayList<>());
    private static final Dish domainDish3 = new Dish(DISH_3_ID, "domain dish3", "", DOMAIN_CAT_2,
            true, Arrays.asList(productRef1, productRef2, productRef3));

    private static final DishView dishView1 = DishView.builder().id(domainDish1.getDishId())
            .name(domainDish1.getName()).description("").categoryId(domainDish1.getCategory().getCategoryId())
            .calorific(6.83f).proteins(6.83f).fats(4.85f).carbs(18.2f).weight(100.0f)
            .products(Arrays.asList(productView1, productView2, productView3)).build();
    private static final DishView dishView2 = DishView.builder().id(domainDish2.getDishId())
            .name(domainDish2.getName()).description("").categoryId(domainDish2.getCategory().getCategoryId())
            .calorific(0f).proteins(0f).fats(0f).carbs(0f).weight(0f)
            .products(new ArrayList<>()).build();
    private static final DishView dishView3 = DishView.builder().id(domainDish3.getDishId())
        .name(domainDish3.getName()).description("").categoryId(domainDish3.getCategory().getCategoryId())
            .calorific(6.83f).proteins(6.83f).fats(4.85f).carbs(18.2f).weight(100.0f)
            .products(Arrays.asList(productView1, productView2, productView3)).build();

    private static final ProductItem dishProduct1 = ProductItem.builder()
            .productId(productRef1.getProductId()).weight(productRef1.getWeight()).build();
    private static final ProductItem dishProduct2 = ProductItem.builder()
            .productId(productRef2.getProductId()).weight(productRef2.getWeight()).build();

    private static final ProductItem dishProduct3 = ProductItem.builder()
            .productId(productRef3.getProductId()).weight(productRef3.getWeight()).build();

    @Test
    public void getAllDishesTest() {
        List<DishCategoryView> dishCategoryViewList = Arrays.asList(SIMPLE_CAT_1, SIMPLE_CAT_2, SIMPLE_CAT_3);
        List<Dish> domainDishList = Arrays.asList(domainDish1, domainDish2, domainDish3);

        when(dishCategories.getDishCategories()).thenReturn(dishCategoryViewList);
        when(dishDomainService.getAllTemplateDishes()).thenReturn(domainDishList);
        when(productService.mapProductRef(productRef1)).thenReturn(productView1);
        when(productService.mapProductRef(productRef2)).thenReturn(productView2);
        when(productService.mapProductRef(productRef3)).thenReturn(productView3);

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

        List<CategoryWithDishes> actual = dishService.getAllTemplateDishes();
        assertEquals(expected, actual);

        verify(dishCategories).getDishCategories();
        verify(dishDomainService).getAllTemplateDishes();
        verify(productService, times(2)).mapProductRef(productRef1);
        verify(productService, times(2)).mapProductRef(productRef2);
        verify(productService, times(2)).mapProductRef(productRef3);
    }

    @Test
    public void getDishTest() {
        when(dishDomainService.getDish(domainDish1.getDishId())).thenReturn(Optional.of(domainDish1));
        when(productService.mapProductRef(productRef1)).thenReturn(productView1);
        when(productService.mapProductRef(productRef2)).thenReturn(productView2);
        when(productService.mapProductRef(productRef3)).thenReturn(productView3);

        DishView actualDishView = dishService.getDish(domainDish1.getDishId());
        assertEquals(dishView1, actualDishView);

        verify(dishDomainService).getDish(domainDish1.getDishId());
        verify(productService).mapProductRef(productRef1);
        verify(productService).mapProductRef(productRef2);
        verify(productService).mapProductRef(productRef3);
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
        List<ProductItem> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        List<ProductRef> dishProductRefList = Arrays.asList(productRef1, productRef2, productRef3);
        Dish newDish = domainDish1.toBuilder().dishId(0).products(dishProductRefList).build();
        DishInfo dishInfoToAdd = DishInfo.builder().name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();
        DishInfo expectedDishInfo = DishInfo.builder().id(domainDish1.getDishId()).name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();

        when(productService.buildMockProducts(dishProductList)).thenReturn(dishProductRefList);
        when(dishDomainService.addTemplateDish(newDish)).thenReturn(domainDish1);

        DishInfo actualDishInfo = dishService.addTemplateDish(dishInfoToAdd);
        assertEquals(expectedDishInfo, actualDishInfo);

        verify(productService).buildMockProducts(dishProductList);
        verify(dishDomainService).addTemplateDish(any());
    }

    @Test
    public void updateDishTest() {
        List<ProductItem> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        List<ProductRef> dishProductRefList = Arrays.asList(productRef1, productRef2, productRef3);
        Dish dishToUpdate = domainDish1.toBuilder().products(dishProductRefList).build();
        DishInfo dishInfoToUpdate = DishInfo.builder().id(domainDish1.getDishId()).name(domainDish1.getName())
                .categoryId(domainDish1.getCategory().getCategoryId()).products(dishProductList).build();

        when(productService.buildMockProducts(dishProductList)).thenReturn(dishProductRefList);
        doNothing().when(dishDomainService).updateDish(7L, dishToUpdate);

        dishService.updateDish(7L, dishInfoToUpdate);
        verify(productService).buildMockProducts(dishProductList);
        verify(dishDomainService).updateDish(eq(7L), any());
    }

    @Test
    public void deleteDishTest() {
        doNothing().when(dishDomainService).deleteTemplateDish(DISH_1_ID);
        dishService.deleteTemplateDish(DISH_1_ID);
        verify(dishDomainService).deleteTemplateDish(DISH_1_ID);
    }
}
