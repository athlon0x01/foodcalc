package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.model.SimpleProductCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link ProductCategoryService} class
 *
 * @author Anton Borovyk.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceTestsConfig.class)
public class ProductCategoryServiceTest {

    private static final int CATEGORY_1_ID = 12345;
    private static final int CATEGORY_2_ID = 54321;
    private static final String CATEGORY_1_NAME = "First category";
    private static final String CATEGORY_2_NAME = "Second category";

    @Autowired
    private ProductCategoryService categoryService;

    @Autowired
    private ProductCategoryDomainService categoryDomainService;

    @Test
    public void getCategories() throws Exception {
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

        verify(categoryDomainService, times(1)).getCategories();
    }

    @Test
    public void addCategory() throws Exception {
        ProductCategory domainCategory = new ProductCategory(-1, CATEGORY_1_NAME);
        when(categoryDomainService.addCategory(domainCategory)).thenReturn(true);
        assertTrue(categoryService.addCategory(CATEGORY_1_NAME));
        verify(categoryDomainService, times(1)).addCategory(domainCategory);
    }

    @Test
    public void updateCategory() throws Exception {
        ProductCategory domainCategory = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        SimpleProductCategory model = new SimpleProductCategory();
        model.id = CATEGORY_1_ID;
        model.name = CATEGORY_1_NAME;

        when(categoryDomainService.updateCategory(domainCategory)).thenReturn(false);
        assertFalse(categoryService.updateCategory(model));
        verify(categoryDomainService, times(1)).updateCategory(domainCategory);
    }

    @Test
    public void deleteCategory() throws Exception {
        ProductCategory domainCategory = new ProductCategory(CATEGORY_1_ID, CATEGORY_1_NAME);
        SimpleProductCategory model = new SimpleProductCategory();
        model.id = CATEGORY_1_ID;
        model.name = CATEGORY_1_NAME;

        when(categoryDomainService.deleteCategory(domainCategory)).thenReturn(true);
        assertTrue(categoryService.deleteCategory(model));
        verify(categoryDomainService, times(1)).deleteCategory(domainCategory);
    }

}