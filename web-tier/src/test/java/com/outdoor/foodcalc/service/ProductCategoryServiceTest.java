package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.product.SimpleProductCategory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductCategoryService} class
 *
 * @author Anton Borovyk.
 */
public class ProductCategoryServiceTest {

    private static final long CATEGORY_1_ID = 12345;
    private static final long CATEGORY_2_ID = 54321;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";

    private static final ProductCategory PRODUCT_CATEGORY_1 = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);

    private static final ProductCategory PRODUCT_CATEGORY_2 = new ProductCategory(CATEGORY_2_ID, CATEGORY_2_NAME);

    private static final SimpleProductCategory SIMPLE_PRODUCT_CATEGORY_1 = new SimpleProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);

    private static final SimpleProductCategory SIMPLE_PRODUCT_CATEGORY_2 = new SimpleProductCategory(CATEGORY_2_ID, CATEGORY_2_NAME);

    @InjectMocks
    private ProductCategoryService categoryService;

    @Mock
    private ProductCategoryDomainService categoryDomainService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCategoriesTest() {
        List<ProductCategory> domainCategories = Arrays.asList(PRODUCT_CATEGORY_1, PRODUCT_CATEGORY_2);
        List<SimpleProductCategory> expected = Arrays.asList(SIMPLE_PRODUCT_CATEGORY_1, SIMPLE_PRODUCT_CATEGORY_2);

        when(categoryDomainService.getCategories()).thenReturn(domainCategories);

        List<SimpleProductCategory> actual = categoryService.getCategories();
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);

        verify(categoryDomainService).getCategories();
    }

    @Test
    public void getCategoryTest() {
        when(categoryDomainService.getCategory(CATEGORY_1_ID))
                .thenReturn(Optional.of(PRODUCT_CATEGORY_1));

        SimpleProductCategory expected = SIMPLE_PRODUCT_CATEGORY_1;

        SimpleProductCategory actual = categoryService.getCategory(CATEGORY_1_ID);
        assertNotNull(actual);
        assertEquals(expected, actual);

        verify(categoryDomainService).getCategory(CATEGORY_1_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotExistingCategoryTest() {
        Optional<ProductCategory> domainCategory = Optional.empty();
        when(categoryDomainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        categoryService.getCategory(CATEGORY_1_ID);
    }

    @Test
    public void addCategoryTest() {
        ProductCategory domainCategory = new ProductCategory(-1, CATEGORY_1_NAME);
        when(categoryDomainService.addCategory(domainCategory)).thenReturn(PRODUCT_CATEGORY_1);
        SimpleProductCategory expected = SIMPLE_PRODUCT_CATEGORY_1;

        SimpleProductCategory actual = categoryService.addCategory(CATEGORY_1_NAME);
        assertEquals(expected, actual);

        verify(categoryDomainService).addCategory(domainCategory);
    }

    @Test
    public void updateCategoryTest() {
        ProductCategory domainCategory = PRODUCT_CATEGORY_1;

        when(categoryDomainService.updateCategory(domainCategory)).thenReturn(false);

        assertFalse(categoryService.updateCategory(SIMPLE_PRODUCT_CATEGORY_1));

        verify(categoryDomainService).updateCategory(domainCategory);
    }

    @Test
    public void deleteCategoryTest() {
        when(categoryDomainService.deleteCategory(CATEGORY_1_ID)).thenReturn(true);
        assertTrue(categoryService.deleteCategory(CATEGORY_1_ID));
        verify(categoryDomainService).deleteCategory(CATEGORY_1_ID);
    }
}