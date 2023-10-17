package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link DishCategoryService} class
 *
 * @author Olga Borovyk.
 */
public class DishCategoryServiceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final long CATEGORY_1_ID = 11111;
    private static final long CATEGORY_2_ID = 22222;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";

    private static final DishCategory DOMAIN_CAT_1 = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final DishCategory DOMAIN_CAT_2 = new DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);

    private static final SimpleDishCategory SIMPLE_DISH_CAT_1 = new SimpleDishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
    private static final SimpleDishCategory SIMPLE_DISH_CAT_2 = new SimpleDishCategory(CATEGORY_2_ID, CATEGORY_2_NAME);

    @Mock
    private DishCategoryDomainService domainService;

    @InjectMocks
    private DishCategoryService categoryService;

    @Test
    public void getDishCategoriesTest() {
        List<DishCategory> domainCategories = Arrays.asList(DOMAIN_CAT_1, DOMAIN_CAT_2);
        List<SimpleDishCategory> expected = Arrays.asList(SIMPLE_DISH_CAT_1, SIMPLE_DISH_CAT_2);

        when(domainService.getCategories()).thenReturn(domainCategories);

        List<SimpleDishCategory> actual = categoryService.getDishCategories();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);

        verify(domainService).getCategories();
    }

    @Test
    public void getDishCategoryTest() {
        Optional<DishCategory> domainCategory = Optional.of(DOMAIN_CAT_1);

        when(domainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        SimpleDishCategory actual = categoryService.getDishCategory(CATEGORY_1_ID);

        assertNotNull(actual);
        assertEquals(SIMPLE_DISH_CAT_1, actual);

        verify(domainService).getCategory(CATEGORY_1_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotExistingDishCategoryTest() {
        Optional<DishCategory> domainCategory = Optional.empty();
        when(domainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        categoryService.getDishCategory(CATEGORY_1_ID);
    }

    @Test
    public void addDishCategoryTest() {
        DishCategory categoryToAdd = new DishCategory(-1, CATEGORY_1_NAME);
        when(domainService.addCategory(categoryToAdd)).thenReturn(DOMAIN_CAT_1);

        SimpleDishCategory actual = categoryService.addDishCategory(CATEGORY_1_NAME);

        assertNotNull(actual);
        assertEquals(SIMPLE_DISH_CAT_1, actual);

        verify(domainService).addCategory(categoryToAdd);
    }

    @Test
    public void updateDishCategoryTest() {
        when(domainService.updateCategory(DOMAIN_CAT_1)).thenReturn(true);

        assertTrue(categoryService.updateDishCategory(SIMPLE_DISH_CAT_1));

        verify(domainService).updateCategory(DOMAIN_CAT_1);
    }

    @Test
    public void deleteDishCategoryTest() {
        when(domainService.deleteCategory(CATEGORY_1_ID)).thenReturn(true);

        assertTrue(categoryService.deleteDishCategory(CATEGORY_1_ID));

        verify(domainService).deleteCategory(CATEGORY_1_ID);
    }
}
