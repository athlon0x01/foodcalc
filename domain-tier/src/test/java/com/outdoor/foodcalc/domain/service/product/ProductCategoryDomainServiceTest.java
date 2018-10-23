package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductCategoryDomainService} class
 *
 * @author Anton Borovyk
 */
public class ProductCategoryDomainServiceTest {

    private static final long CATEGORY_ID = 12345;
    private static final ProductCategory dummyCategory = new ProductCategory(CATEGORY_ID, "dummyCategory");

    @InjectMocks
    private ProductCategoryDomainService categoryService;

    @Mock
    private IProductCategoryRepo categoryRepo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCategoriesTest() {
        List<ProductCategory> expected = Collections.singletonList(dummyCategory);

        when(categoryRepo.getCategories()).thenReturn(expected);

        List<ProductCategory> actual = categoryService.getCategories();
        assertEquals(expected, actual);

        verify(categoryRepo, times(1)).getCategories();
    }

    @Test
    public void getCategoryTest() {
        Optional<ProductCategory> expected = Optional.of(dummyCategory);

        when(categoryRepo.getCategory(CATEGORY_ID)).thenReturn(expected);

        Optional<ProductCategory> actual = categoryService.getCategory(CATEGORY_ID);
        assertEquals(expected, actual);

        verify(categoryRepo, times(1)).getCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() {
        when(categoryRepo.addCategory(dummyCategory)).thenReturn(CATEGORY_ID);
        assertEquals(dummyCategory, categoryService.addCategory(dummyCategory));
        verify(categoryRepo, times(1)).addCategory(dummyCategory);
    }

    @Test
    public void updateCategoryTest() {
        when(categoryRepo.updateCategory(dummyCategory)).thenReturn(false);
        assertFalse(categoryService.updateCategory(dummyCategory));
        verify(categoryRepo, times(1)).updateCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() {
        when(categoryRepo.deleteCategory(CATEGORY_ID)).thenReturn(true);
        assertTrue(categoryService.deleteCategory(CATEGORY_ID));
        verify(categoryRepo, times(1)).deleteCategory(CATEGORY_ID);
    }

}