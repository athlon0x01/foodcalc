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

import static org.junit.Assert.assertTrue;
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

    private static final DishCategory CATEGORY1 = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);

    private static final DishCategory CATEGORY2 = new DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);

    private static final DishCategory CATEGORY3 = new DishCategory(CATEGORY_3_ID, CATEGORY_3_NAME);

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

    private static final long DISH_1_ID = 22222;
    private static final long DISH_2_ID = 33333;
    private static final long DISH_3_ID = 44444;

    @Test
    public void getAllDishesTest() {
        SimpleDishCategory cat1 = createSimpleDishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        SimpleDishCategory cat2 = createSimpleDishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);
        SimpleDishCategory cat3 = createSimpleDishCategory(CATEGORY_3_ID, CATEGORY_3_NAME);
        List<SimpleDishCategory> dishCategoryList = Arrays.asList(cat1, cat2, cat3);
        List<ProductRef> domainProducts = Arrays.asList(productRef1, productRef2, productRef3);
        Dish domainDish1 = new Dish(DISH_1_ID, "domain dish1", "", CATEGORY1, domainProducts);
        Dish domainDish2 = new Dish(DISH_2_ID, "domain dish2", "", CATEGORY1, new ArrayList<>());
        Dish domainDish3 = new Dish(DISH_3_ID, "domain dish3", "", CATEGORY2, domainProducts);
        List<Dish> domainDishList = Arrays.asList(domainDish1, domainDish2, domainDish3);
        when(dishCategories.getDishCategories()).thenReturn(dishCategoryList);
        when(dishDomainService.getAllDishes()).thenReturn(domainDishList);

        ProductView productView1 = createProductView(productRef1);
        ProductView productView2 = createProductView(productRef2);
        ProductView productView3 = createProductView(productRef3);
        List<ProductView> productViewList = Arrays.asList(productView1, productView2, productView3);
        DishView dishView1 = createDishView(DISH_1_ID, "domain dish1", CATEGORY1.getCategoryId(), productViewList);
        DishView dishView2 = createDishView(DISH_2_ID, "domain dish2", CATEGORY1.getCategoryId(), new ArrayList<>());
        DishView dishView3 = createDishView(DISH_3_ID, "domain dish3", CATEGORY2.getCategoryId(), productViewList);
        when(productService.getProduct(product1.getProductId())).thenReturn(productView1);
        when(productService.getProduct(product2.getProductId())).thenReturn(productView2);
        when(productService.getProduct(product3.getProductId())).thenReturn(productView3);

        CategoryWithDishes categoryWithDishes1 = createCategoryWithDishes(
                CATEGORY1.getCategoryId(), CATEGORY1.getName(), Arrays.asList(dishView1, dishView2));
        CategoryWithDishes categoryWithDishes2 = createCategoryWithDishes(
                CATEGORY2.getCategoryId(), CATEGORY2.getName(), Arrays.asList(dishView3));
        CategoryWithDishes categoryWithDishes3 = createCategoryWithDishes(
                CATEGORY3.getCategoryId(), CATEGORY3.getName(), new ArrayList<>());
        List<CategoryWithDishes> expected = Arrays.asList(categoryWithDishes1, categoryWithDishes2, categoryWithDishes3);

        List<CategoryWithDishes> actual = dishService.getAllDishes();
        assertTrue(assertEqualsDishView(expected, actual));

        verify(dishCategories).getDishCategories();
        verify(dishDomainService).getAllDishes();
        verify(productService, times(2)).getProduct(product1.getProductId());
        verify(productService, times(2)).getProduct(product2.getProductId());
        verify(productService, times(2)).getProduct(product3.getProductId());
    }

    @Test
    public void getDishTest() {
        List<ProductRef> domainProducts = Arrays.asList(productRef1, productRef2, productRef3);
        ProductView productView1 = createProductView(productRef1);
        ProductView productView2 = createProductView(productRef2);
        ProductView productView3 = createProductView(productRef3);
        List<ProductView> productViewList = Arrays.asList(productView1, productView2, productView3);
        Dish domainDish = new Dish(DISH_1_ID, "test domain dish", "", CATEGORY1, domainProducts);
        DishView expectedDishView = createDishView(
                DISH_1_ID, "test domain dish", CATEGORY1.getCategoryId(), productViewList);
        when(dishDomainService.getDish(DISH_1_ID)).thenReturn(Optional.of(domainDish));
        when(productService.getProduct(product1.getProductId())).thenReturn(productView1);
        when(productService.getProduct(product2.getProductId())).thenReturn(productView2);
        when(productService.getProduct(product3.getProductId())).thenReturn(productView3);

        DishView actualDishView = dishService.getDish(DISH_1_ID);
        assertTrue(assertEqualsDishView(expectedDishView, actualDishView));

        verify(dishDomainService).getDish(DISH_1_ID);
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
        DishProduct dishProduct1 = createDishProduct(productRef1);
        DishProduct dishProduct2 = createDishProduct(productRef2);
        DishProduct dishProduct3 = createDishProduct(productRef3);
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        SimpleDish simpleDishToAdd = createSimpleDish(77777, "test simpleDish", CATEGORY_1_ID, dishProductList);
        SimpleDish expectedSimpleDish = createSimpleDish(DISH_1_ID, "test simpleDish", CATEGORY_1_ID, dishProductList);
        Dish addedDomainDish = new Dish(DISH_1_ID, "test simpleDish", "",
                CATEGORY1, Arrays.asList(productRef1,productRef2, productRef3));
        when(dishCategoryDomainService.getCategory(simpleDishToAdd.categoryId))
                .thenReturn(Optional.of(addedDomainDish.getCategory()));
        when(dishDomainService.addDish(any())).thenReturn(addedDomainDish);
        when(productService.getDomainProduct(dishProduct1.productId)).thenReturn(product1);
        when(productService.getDomainProduct(dishProduct2.productId)).thenReturn(product2);
        when(productService.getDomainProduct(dishProduct3.productId)).thenReturn(product3);

        SimpleDish actualSimpleDish = dishService.addDish(simpleDishToAdd);
        assertTrue(assertEqualsSimpleDish(expectedSimpleDish, actualSimpleDish));

        verify(dishCategoryDomainService).getCategory(simpleDishToAdd.categoryId);
        verify(dishDomainService).addDish(any());
        verify(productService).getDomainProduct(dishProduct1.productId);
        verify(productService).getDomainProduct(dishProduct2.productId);
        verify(productService).getDomainProduct(dishProduct3.productId);
    }

    @Test(expected = NotFoundException.class)
    public void addDishCategoryFailTest() {
        DishProduct dishProduct1 = createDishProduct(productRef1);
        DishProduct dishProduct2 = createDishProduct(productRef2);
        DishProduct dishProduct3 = createDishProduct(productRef3);
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        SimpleDish simpleDishToAdd = createSimpleDish(77777, "test simpleDish", CATEGORY_1_ID, dishProductList);
        when(dishCategoryDomainService.getCategory(simpleDishToAdd.categoryId)).thenReturn(Optional.empty());

        dishService.addDish(simpleDishToAdd);
    }

    @Test
    public void updateDishTest() {
        DishProduct dishProduct1 = createDishProduct(productRef1);
        DishProduct dishProduct2 = createDishProduct(productRef2);
        DishProduct dishProduct3 = createDishProduct(productRef3);
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        SimpleDish simpleDishToUpdate = createSimpleDish(DISH_1_ID, "test simpleDish", CATEGORY_1_ID, dishProductList);
        when(dishCategoryDomainService.getCategory(simpleDishToUpdate.categoryId)).thenReturn(Optional.of(CATEGORY1));
        when(productService.getDomainProduct(dishProduct1.productId)).thenReturn(product1);
        when(productService.getDomainProduct(dishProduct2.productId)).thenReturn(product2);
        when(productService.getDomainProduct(dishProduct3.productId)).thenReturn(product3);
        doNothing().when(dishDomainService).updateDish(any());

        dishService.updateDish(simpleDishToUpdate);
        verify(dishDomainService).updateDish(any());

    }

    @Test(expected = NotFoundException.class)
    public void updateDishCategoryFailTest() {
        DishProduct dishProduct1 = createDishProduct(productRef1);
        DishProduct dishProduct2 = createDishProduct(productRef2);
        DishProduct dishProduct3 = createDishProduct(productRef3);
        List<DishProduct> dishProductList = Arrays.asList(dishProduct1, dishProduct2, dishProduct3);
        SimpleDish simpleDishToUpdate = createSimpleDish(DISH_1_ID, "test simpleDish", CATEGORY_1_ID, dishProductList);
        when(dishCategoryDomainService.getCategory(simpleDishToUpdate.categoryId)).thenReturn(Optional.empty());

        dishService.updateDish(simpleDishToUpdate);
    }

    @Test
    public void deleteDishTest() {
        doNothing().when(dishDomainService).deleteDish(DISH_1_ID);
        dishService.deleteDish(DISH_1_ID);
        verify(dishDomainService).deleteDish(DISH_1_ID);
    }

    private DishView createDishView(long id, String name, long categoryId, List<ProductView> products) {
        DishView dishView = new DishView();
        dishView.id = id;
        dishView.name = name;
        dishView.categoryId = categoryId;
        dishView.products = products;
        return dishView;
    }

    private SimpleDish createSimpleDish(long id, String name, long categoryId, List<DishProduct> products) {
        SimpleDish simpleDish = new SimpleDish();
        simpleDish.id = id;
        simpleDish.name = name;
        simpleDish.categoryId = categoryId;
        simpleDish.products = products;
        return simpleDish;
    }

    private DishProduct createDishProduct(ProductRef productRef) {
        DishProduct dishProduct = new DishProduct();
        dishProduct.productId = productRef.getProductId();
        dishProduct.weight = productRef.getWeight();
        return dishProduct;
    }

    private ProductView createProductView(ProductRef productRef) {
        ProductView productView = new ProductView();
        productView.id = productRef.getProductId();
        productView.name = productRef.getName();
        productView.categoryId = productRef.getProductCategoryId();
        productView.calorific = productRef.getCalorific();
        productView.proteins = productRef.getProteins();
        productView.fats = productRef.getFats();
        productView.carbs = productRef.getCarbs();
        productView.weight = productRef.getWeight();
        return productView;
    }

    private SimpleDishCategory createSimpleDishCategory(long id, String name) {
        SimpleDishCategory simpleDishCategory = new SimpleDishCategory();
        simpleDishCategory.id = id;
        simpleDishCategory.name = name;
        return simpleDishCategory;
    }

    private CategoryWithDishes createCategoryWithDishes(long id, String name, List<DishView> dishes) {
        CategoryWithDishes categoryWithDishes = new CategoryWithDishes();
        categoryWithDishes.id = id;
        categoryWithDishes.name = name;
        categoryWithDishes.dishes = dishes;
        return categoryWithDishes;
    }

    private boolean assertEqualsDishView(DishView expected, DishView actual) {
        if (expected == actual) return true;
        if (actual == null || expected.getClass() != actual.getClass()) return false;
        return expected.id == actual.id &&
                expected.name.equals(actual.name) &&
                expected.categoryId == actual.categoryId &&
                expected.products.equals(actual.products);
    }

    private boolean assertEqualsSimpleDish(SimpleDish expected, SimpleDish actual) {
        if (expected == actual) return true;
        if (actual == null || expected.getClass() != actual.getClass()) return false;
        return expected.id == actual.id &&
                expected.name.equals(actual.name) &&
                expected.categoryId == actual.categoryId &&
                assertEqualsDishProductList(expected.products, actual.products);
    }

    private boolean assertEqualsDishProducts(DishProduct expected, DishProduct actual) {
        if (expected == actual) return true;
        if (expected == null || actual == null || expected.getClass() != actual.getClass()) return false;
        return expected.productId == actual.productId &&
                Float.compare(expected.weight, actual.weight) == 0;
    }

    private boolean assertEqualsDishProductList(List<DishProduct> expected, List<DishProduct> actual) {
        if (expected.size() != actual.size()) return false;
        if (expected.getClass() != actual.getClass()) return false;
        for (int i =0; i < expected.size(); i++) {
            if (!assertEqualsDishProducts(expected.get(i), actual.get(i))) return false;
        }
        return true;
    }

    private boolean assertEqualsDishView(List<CategoryWithDishes> expected, List<CategoryWithDishes> actual) {
        if (expected.size() != actual.size()) return false;
        for (int i =0; i < expected.size(); i++) {
            if (expected.get(i).id != actual.get(i).id) return false;
            if (!Objects.equals(expected.get(i).name, actual.get(i).name)) return false;
            if (expected.get(i).dishes.size() != actual.get(i).dishes.size()) return false;
            for (int j =0; j < expected.get(i).dishes.size(); j++) {
                if (!assertEqualsDishView(expected.get(i).dishes.get(j), actual.get(i).dishes.get(j))) return false;
            }
        }
        return true;
    }
}
