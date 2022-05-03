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
    private DishCategoryDomainService domainService;

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
        when(domainService.getCategories()).thenReturn(domainCategories);

        List<SimpleDishCategory> actual = categoryService.getDishCategories();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(CATEGORY_1_ID, actual.get(0).id);
        assertEquals(CATEGORY_1_NAME, actual.get(0).name);
        assertEquals(CATEGORY_2_ID, actual.get(1).id);
        assertEquals(CATEGORY_2_NAME, actual.get(1).name);

        verify(domainService).getCategories();
    }

    @Test
    public void getDishCategoryTest() {
        Optional<DishCategory> domainCategory = Optional.of(new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME));

        when(domainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        SimpleDishCategory actual = categoryService.getDishCategory(CATEGORY_1_ID);

        assertNotNull(actual);
        assertEquals(CATEGORY_1_ID, actual.id);
        assertEquals(CATEGORY_1_NAME, actual.name);

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
        DishCategory domainCategory = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        DishCategory categoryToAdd = new DishCategory(-1, CATEGORY_1_NAME);
        when(domainService.addCategory(categoryToAdd)).thenReturn(domainCategory);

        SimpleDishCategory actual = categoryService.addDishCategory(CATEGORY_1_NAME);

        assertNotNull(actual);
        assertEquals(CATEGORY_1_ID, actual.id);
        assertEquals(CATEGORY_1_NAME, actual.name);

        verify(domainService).addCategory(categoryToAdd);
    }

    @Test
    public void updateDishCategoryTest() {
        SimpleDishCategory categoryToUpdate = new SimpleDishCategory();
        categoryToUpdate.id = CATEGORY_1_ID;
        categoryToUpdate.name = CATEGORY_1_NAME;
        DishCategory domainCategory = new DishCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        when(domainService.updateCategory(domainCategory)).thenReturn(true);

        assertTrue(categoryService.updateDishCategory(categoryToUpdate));

        verify(domainService).updateCategory(domainCategory);
    }

    @Test
    public void deleteDishCategoryTest() {
        when(domainService.deleteCategory(CATEGORY_1_ID)).thenReturn(true);

        assertTrue(categoryService.deleteDishCategory(CATEGORY_1_ID));

        verify(domainService).deleteCategory(CATEGORY_1_ID);
    }
}
