package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.product.ProductCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductCategoryService} class
 *
 * @author Anton Borovyk.
 */
@ExtendWith(MockitoExtension.class)
public class ProductCategoryServiceTest {

    private static final long CATEGORY_1_ID = 12345;
    private static final long CATEGORY_2_ID = 54321;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";

    private static final com.outdoor.foodcalc.domain.model.product.ProductCategory PRODUCT_CATEGORY_1 = new com.outdoor.foodcalc.domain.model.product.ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);

    private static final com.outdoor.foodcalc.domain.model.product.ProductCategory PRODUCT_CATEGORY_2 = new com.outdoor.foodcalc.domain.model.product.ProductCategory(CATEGORY_2_ID, CATEGORY_2_NAME);

    private static final ProductCategory SIMPLE_PRODUCT_CATEGORY_1 = ProductCategory.builder().id(CATEGORY_1_ID).name(CATEGORY_1_NAME).build();

    private static final ProductCategory SIMPLE_PRODUCT_CATEGORY_2 = ProductCategory.builder().id(CATEGORY_2_ID).name(CATEGORY_2_NAME).build();

    @InjectMocks
    private ProductCategoryService categoryService;

    @Mock
    private ProductCategoryDomainService categoryDomainService;

    @Test
    public void getCategoriesTest() {
        List<com.outdoor.foodcalc.domain.model.product.ProductCategory> domainCategories = Arrays.asList(PRODUCT_CATEGORY_1, PRODUCT_CATEGORY_2);
        List<ProductCategory> expected = Arrays.asList(SIMPLE_PRODUCT_CATEGORY_1, SIMPLE_PRODUCT_CATEGORY_2);

        when(categoryDomainService.getCategories()).thenReturn(domainCategories);

        List<ProductCategory> actual = categoryService.getCategories();
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);

        verify(categoryDomainService).getCategories();
    }

    @Test
    public void getCategoryTest() {
        when(categoryDomainService.getCategory(CATEGORY_1_ID))
                .thenReturn(Optional.of(PRODUCT_CATEGORY_1));

        ProductCategory expected = SIMPLE_PRODUCT_CATEGORY_1;

        ProductCategory actual = categoryService.getCategory(CATEGORY_1_ID);
        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(categoryDomainService).getCategory(CATEGORY_1_ID);
    }

    @Test
    public void getNotExistingCategoryTest() {
        Optional<com.outdoor.foodcalc.domain.model.product.ProductCategory> domainCategory = Optional.empty();
        when(categoryDomainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.getCategory(CATEGORY_1_ID);
        });
    }

    @Test
    public void addCategoryTest() {
        com.outdoor.foodcalc.domain.model.product.ProductCategory domainCategory = new com.outdoor.foodcalc.domain.model.product.ProductCategory(-1, CATEGORY_1_NAME);
        when(categoryDomainService.addCategory(domainCategory)).thenReturn(PRODUCT_CATEGORY_1);
        ProductCategory expected = SIMPLE_PRODUCT_CATEGORY_1;

        ProductCategory actual = categoryService.addCategory(CATEGORY_1_NAME);
        assertEquals(expected, actual);

        verify(categoryDomainService).addCategory(domainCategory);
    }

    @Test
    public void updateCategoryTest() {
        doNothing().when(categoryDomainService).updateCategory(PRODUCT_CATEGORY_1);

        categoryService.updateCategory(SIMPLE_PRODUCT_CATEGORY_1);

        verify(categoryDomainService).updateCategory(PRODUCT_CATEGORY_1);
    }

    @Test
    public void updateCategoryNotFoundTest() {
        doThrow(NotFoundException.class).when(categoryDomainService).updateCategory(PRODUCT_CATEGORY_1);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.updateCategory(SIMPLE_PRODUCT_CATEGORY_1);
        });
    }

    @Test
    public void updateCategoryFailTest() {
        doThrow(FoodcalcDomainException.class).when(categoryDomainService).updateCategory(PRODUCT_CATEGORY_1);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            categoryService.updateCategory(SIMPLE_PRODUCT_CATEGORY_1);
        });
    }

    @Test
    public void deleteCategoryTest() {
        doNothing().when(categoryDomainService).deleteCategory(CATEGORY_1_ID);

        categoryService.deleteCategory(CATEGORY_1_ID);

        verify(categoryDomainService).deleteCategory(CATEGORY_1_ID);
    }

    @Test
    public void deleteDishCategoryNotFoundTest() {
        doThrow(NotFoundException.class).when(categoryDomainService).deleteCategory(CATEGORY_1_ID);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.deleteCategory(CATEGORY_1_ID);
        });
    }

    @Test
    public void deleteDishCategoryFailTest() {
        doThrow(FoodcalcDomainException.class).when(categoryDomainService).deleteCategory(CATEGORY_1_ID);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            categoryService.deleteCategory(CATEGORY_1_ID);
        });
    }
}