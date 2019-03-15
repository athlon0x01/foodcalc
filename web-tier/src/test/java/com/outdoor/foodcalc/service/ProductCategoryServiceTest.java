package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.SimpleProductCategory;
import org.junit.Before;
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

    @InjectMocks
    private ProductCategoryService categoryService;

    @Mock
    private ProductCategoryDomainService categoryDomainService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCategories() {
        List<ProductCategory> domainCategories = Arrays.asList(
            new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME),
            new ProductCategory(CATEGORY_2_ID, CATEGORY_2_NAME)
        );
        SimpleProductCategory category1 = new SimpleProductCategory();
        category1.id = CATEGORY_1_ID;
        category1.name = CATEGORY_1_NAME;
        SimpleProductCategory category2 = new SimpleProductCategory();
        category2.id = CATEGORY_2_ID;
        category2.name = CATEGORY_2_NAME;

        when(categoryDomainService.getCategories()).thenReturn(domainCategories);

        List<SimpleProductCategory> actual = categoryService.getCategories();
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(CATEGORY_1_ID, actual.get(0).id);
        assertEquals(CATEGORY_1_NAME, actual.get(0).name);
        assertEquals(CATEGORY_2_ID, actual.get(1).id);
        assertEquals(CATEGORY_2_NAME, actual.get(1).name);

        verify(categoryDomainService).getCategories();
    }

    @Test
    public void getCategory() {
        ProductCategory category = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        Optional<ProductCategory> domainCategory = Optional.of(category);
        SimpleProductCategory category1 = new SimpleProductCategory();
        category1.id = CATEGORY_1_ID;
        category1.name = CATEGORY_1_NAME;

        when(categoryDomainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        SimpleProductCategory actual = categoryService.getCategory(CATEGORY_1_ID);
        assertNotNull(actual);
        assertEquals(CATEGORY_1_ID, actual.id);
        assertEquals(CATEGORY_1_NAME, actual.name);

        verify(categoryDomainService).getCategory(CATEGORY_1_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getNotExistingCategory() {
        Optional<ProductCategory> domainCategory = Optional.empty();
        when(categoryDomainService.getCategory(CATEGORY_1_ID)).thenReturn(domainCategory);

        categoryService.getCategory(CATEGORY_1_ID);
    }

    @Test
    public void addCategory() {
        ProductCategory domainCategory = new ProductCategory(-1, CATEGORY_1_NAME);
        ProductCategory returnedCategory = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        when(categoryDomainService.addCategory(domainCategory)).thenReturn(returnedCategory);

        SimpleProductCategory category = categoryService.addCategory(CATEGORY_1_NAME);
        assertEquals(CATEGORY_1_ID, category.id);
        assertEquals(CATEGORY_1_NAME, category.name);

        verify(categoryDomainService).addCategory(domainCategory);
    }

    @Test
    public void updateCategory() {
        ProductCategory domainCategory = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        SimpleProductCategory model = new SimpleProductCategory();
        model.id = CATEGORY_1_ID;
        model.name = CATEGORY_1_NAME;

        when(categoryDomainService.updateCategory(domainCategory)).thenReturn(false);
        assertFalse(categoryService.updateCategory(model));
        verify(categoryDomainService).updateCategory(domainCategory);
    }

    @Test
    public void deleteCategory() {
        when(categoryDomainService.deleteCategory(CATEGORY_1_ID)).thenReturn(true);
        assertTrue(categoryService.deleteCategory(CATEGORY_1_ID));
        verify(categoryDomainService).deleteCategory(CATEGORY_1_ID);
    }

}