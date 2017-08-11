package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import com.outdoor.foodcalc.domain.service.DomainServiceTestsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DomainServiceTestsConfig.class)
public class ProductCategoryDomainServiceTest {

    private static final long CATEGORY_ID = 12345;
    private static final ProductCategory dummyCategory = new ProductCategory(CATEGORY_ID, "dummyCategory");

    @Autowired
    private ProductCategoryDomainService categoryService;

    @Mock
    private IProductCategoryRepo categoryRepo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(categoryService, "categoryRepo", categoryRepo);
    }

    @Test
    public void getCategoriesTest() throws Exception {
        List<ProductCategory> expected = Collections.singletonList(dummyCategory);

        when(categoryRepo.getCategories()).thenReturn(expected);

        List<ProductCategory> actual = categoryService.getCategories();
        assertEquals(expected, actual);

        verify(categoryRepo, times(1)).getCategories();
    }

    @Test
    public void getCategoryTest() throws Exception {
        Optional<ProductCategory> expected = Optional.of(dummyCategory);

        when(categoryRepo.getCategory(CATEGORY_ID)).thenReturn(expected);

        Optional<ProductCategory> actual = categoryService.getCategory(CATEGORY_ID);
        assertEquals(expected, actual);

        verify(categoryRepo, times(1)).getCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() throws Exception {
        long newId = 54321;
        when(categoryRepo.addCategory(dummyCategory)).thenReturn(newId);
        assertEquals(newId, categoryService.addCategory(dummyCategory));
        verify(categoryRepo, times(1)).addCategory(dummyCategory);
    }

    @Test
    public void updateCategoryTest() throws Exception {
        when(categoryRepo.updateCategory(dummyCategory)).thenReturn(false);
        assertFalse(categoryService.updateCategory(dummyCategory));
        verify(categoryRepo, times(1)).updateCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        when(categoryRepo.deleteCategory(CATEGORY_ID)).thenReturn(true);
        assertTrue(categoryService.deleteCategory(CATEGORY_ID));
        verify(categoryRepo, times(1)).deleteCategory(CATEGORY_ID);
    }

}