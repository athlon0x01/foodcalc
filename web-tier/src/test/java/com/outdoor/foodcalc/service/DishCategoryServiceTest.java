package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private static final long CATEGORY_1_ID = 11111;
    private static final long CATEGORY_2_ID = 22222;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";

    @Mock
    private DishCategoryDomainService categoryDomainService;

    @InjectMocks
    private DishCategoryService categoryService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getDishCategoriesTest() {
        List<DishCategory> domainCategories = Arrays.asList(
                new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME),
                new DishCategory(CATEGORY_2_ID, CATEGORY_2_NAME)
        );
        SimpleDishCategory category1 = new SimpleDishCategory();
        category1.id = CATEGORY_1_ID;
        category1.name = CATEGORY_1_NAME;
        SimpleDishCategory category2 = new SimpleDishCategory();
        category2.id = CATEGORY_2_ID;
        category2.name = CATEGORY_2_NAME;
        when(categoryDomainService.getCategories()).thenReturn(domainCategories);

        List<SimpleDishCategory> actual = categoryService.getDishCategories();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(CATEGORY_1_ID, actual.get(0).id);
        assertEquals(CATEGORY_1_NAME, actual.get(0).name);
        assertEquals(CATEGORY_2_ID, actual.get(1).id);
        assertEquals(CATEGORY_2_NAME, actual.get(1).name);

        verify(categoryDomainService).getCategories();
    }

    @Test
    public void getDishCategoryTest() {
        Optional<DishCategory> domainCategory = Optional.of(new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME));

        when(categoryDomainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        SimpleDishCategory actual = categoryService.getDishCategory(CATEGORY_1_ID);

        assertNotNull(actual);
        assertEquals(CATEGORY_1_ID, actual.id);
        assertEquals(CATEGORY_1_NAME, actual.name);

        verify(categoryDomainService).getCategory(CATEGORY_1_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotExistingDishCategoryTest() {
        Optional<DishCategory> domainCategory = Optional.empty();
        when(categoryDomainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        categoryService.getDishCategory(CATEGORY_1_ID);
    }

    @Test
    public void addDishCategoryTest() {
        DishCategory domainCategory = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        DishCategory categoryToAdd = new DishCategory(-1, CATEGORY_1_NAME);
        when(categoryDomainService.addCategory(categoryToAdd)).thenReturn(domainCategory);

        SimpleDishCategory actual = categoryService.addDishCategory(CATEGORY_1_NAME);

        assertNotNull(actual);
        assertEquals(CATEGORY_1_ID, actual.id);
        assertEquals(CATEGORY_1_NAME, actual.name);

        verify(categoryDomainService).addCategory(categoryToAdd);
    }

    @Test
    public void updateDishCategoryTest() {
        SimpleDishCategory categoryToAdd = new SimpleDishCategory();
        categoryToAdd.id = CATEGORY_1_ID;
        categoryToAdd.name = CATEGORY_1_NAME;
        DishCategory domainCategory = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        boolean expected = true;
        when(categoryDomainService.updateCategory(domainCategory)).thenReturn(expected);

        assertEquals(expected, categoryService.updateDishCategory(categoryToAdd));

        verify(categoryDomainService).updateCategory(domainCategory);
    }

    @Test
    public void deleteDishCategoryTest() {
        when(categoryDomainService.deleteCategory(CATEGORY_1_ID)).thenReturn(true);

        assertTrue(categoryService.deleteDishCategory(CATEGORY_1_ID));

        verify(categoryDomainService).deleteCategory(CATEGORY_1_ID);
    }
}
