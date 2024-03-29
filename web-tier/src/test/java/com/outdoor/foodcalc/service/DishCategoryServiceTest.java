package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.model.dish.DishCategoryView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link DishCategoryService} class
 *
 * @author Olga Borovyk.
 */
@ExtendWith(MockitoExtension.class)
public class DishCategoryServiceTest {

    private static final long CATEGORY_1_ID = 11111;
    private static final long CATEGORY_2_ID = 22222;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";

    private static final DishCategory DOMAIN_CAT_1 = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final DishCategory DOMAIN_CAT_2 = new DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);

    private static final DishCategoryView SIMPLE_DISH_CAT_1 = DishCategoryView.builder().id(CATEGORY_1_ID).name(CATEGORY_1_NAME).build();
    private static final DishCategoryView SIMPLE_DISH_CAT_2 = DishCategoryView.builder().id(CATEGORY_2_ID).name(CATEGORY_2_NAME).build();

    @Mock
    private DishCategoryDomainService domainService;

    @InjectMocks
    private DishCategoryService categoryService;

    @Test
    public void getDishCategoriesTest() {
        List<DishCategory> domainCategories = Arrays.asList(DOMAIN_CAT_1, DOMAIN_CAT_2);
        List<DishCategoryView> expected = Arrays.asList(SIMPLE_DISH_CAT_1, SIMPLE_DISH_CAT_2);

        when(domainService.getCategories()).thenReturn(domainCategories);

        List<DishCategoryView> actual = categoryService.getDishCategories();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);

        verify(domainService).getCategories();
    }

    @Test
    public void getDishCategoryTest() {
        Optional<DishCategory> domainCategory = Optional.of(DOMAIN_CAT_1);

        when(domainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        DishCategoryView actual = categoryService.getDishCategory(CATEGORY_1_ID);

        assertNotNull(actual);
        assertEquals(SIMPLE_DISH_CAT_1, actual);

        verify(domainService).getCategory(CATEGORY_1_ID);
    }

    @Test
    public void getNotExistingDishCategoryTest() {
        Optional<DishCategory> domainCategory = Optional.empty();
        when(domainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.getDishCategory(CATEGORY_1_ID);
        });
    }

    @Test
    public void addDishCategoryTest() {
        DishCategory categoryToAdd = new DishCategory(-1, CATEGORY_1_NAME);
        when(domainService.addCategory(categoryToAdd)).thenReturn(DOMAIN_CAT_1);

        DishCategoryView actual = categoryService.addDishCategory(CATEGORY_1_NAME);

        assertNotNull(actual);
        assertEquals(SIMPLE_DISH_CAT_1, actual);

        verify(domainService).addCategory(categoryToAdd);
    }

    @Test
    public void updateDishCategoryTest() {
        doNothing().when(domainService).updateCategory(DOMAIN_CAT_1);

        categoryService.updateDishCategory(SIMPLE_DISH_CAT_1);

        verify(domainService).updateCategory(DOMAIN_CAT_1);
    }

    @Test
    public void updateDishCategoryNotFoundTest() {
        doThrow(NotFoundException.class).when(domainService).updateCategory(DOMAIN_CAT_1);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.updateDishCategory(SIMPLE_DISH_CAT_1);
        });
    }

    @Test
    public void updateDishCategoryFailTest() {
        doThrow(FoodcalcDomainException.class).when(domainService).updateCategory(DOMAIN_CAT_1);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            categoryService.updateDishCategory(SIMPLE_DISH_CAT_1);
        });
    }

    @Test
    public void deleteDishCategoryTest() {
        doNothing().when(domainService).deleteCategory(CATEGORY_1_ID);

        categoryService.deleteDishCategory(CATEGORY_1_ID);

        verify(domainService).deleteCategory(CATEGORY_1_ID);
    }

    @Test
    public void deleteDishCategoryNotFoundTest() {
        doThrow(NotFoundException.class).when(domainService).deleteCategory(CATEGORY_1_ID);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.deleteDishCategory(CATEGORY_1_ID);
        });
    }

    @Test
    public void deleteDishCategoryFailTest() {
        doThrow(FoodcalcDomainException.class).when(domainService).deleteCategory(CATEGORY_1_ID);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            categoryService.deleteDishCategory(CATEGORY_1_ID);
        });
    }
}
