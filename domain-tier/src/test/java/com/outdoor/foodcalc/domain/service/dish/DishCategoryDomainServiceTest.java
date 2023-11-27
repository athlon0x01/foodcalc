package com.outdoor.foodcalc.domain.service.dish;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.repository.dish.IDishCategoryRepo;
import com.outdoor.foodcalc.domain.service.dish.DishCategoryDomainService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link DishCategoryDomainService} class
 *
 * @author Olga Borovyk
 */
public class DishCategoryDomainServiceTest {

    private static final long CATEGORY_ID = 12345;
    private static final DishCategory dummyCategory = new DishCategory(CATEGORY_ID, "dummy category");

    @InjectMocks
    private DishCategoryDomainService categoryService;

    @Mock
    private IDishCategoryRepo categoryRepo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCategoriesTest() {
        List<DishCategory> expected = Collections.singletonList(dummyCategory);

        when(categoryRepo.getCategories()).thenReturn(expected);

        List<DishCategory> actual = categoryService.getCategories();
        assertEquals(expected, actual);

        verify(categoryRepo).getCategories();
    }

    @Test
    public void getCategoryTest() {
        Optional<DishCategory> expected = Optional.of(dummyCategory);

        when(categoryRepo.getCategory(CATEGORY_ID)).thenReturn(expected);

        Optional<DishCategory> actual = categoryService.getCategory(CATEGORY_ID);
        assertEquals(expected, actual);

        verify(categoryRepo).getCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() {
        DishCategory categoryToAdd = new DishCategory(-1, "dummy category");
        when(categoryRepo.addCategory(categoryToAdd)).thenReturn(CATEGORY_ID);

        assertEquals(dummyCategory, categoryService.addCategory(categoryToAdd));

        verify(categoryRepo).addCategory(categoryToAdd);
    }

    @Test
    public void updateCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(true);
        when(categoryRepo.updateCategory(dummyCategory)).thenReturn(true);

        categoryService.updateCategory(dummyCategory);

        verify(categoryRepo).exist(CATEGORY_ID);
        verify(categoryRepo).updateCategory(dummyCategory);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExistingCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(false);

        categoryService.updateCategory(dummyCategory);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void updateCategoryFailTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(true);
        when(categoryRepo.updateCategory(dummyCategory)).thenReturn(false);

        categoryService.updateCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(true);
        when(categoryRepo.deleteCategory(CATEGORY_ID)).thenReturn(true);

        categoryService.deleteCategory(CATEGORY_ID);

        verify(categoryRepo).exist(CATEGORY_ID);
        verify(categoryRepo).deleteCategory(CATEGORY_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExistingCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(false);

        categoryService.deleteCategory(CATEGORY_ID);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void deleteCategoryFailTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(true);
        when(categoryRepo.deleteCategory(CATEGORY_ID)).thenReturn(false);

        categoryService.deleteCategory(CATEGORY_ID);
    }
}
