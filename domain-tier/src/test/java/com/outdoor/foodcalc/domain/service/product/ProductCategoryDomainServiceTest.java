package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import com.outdoor.foodcalc.domain.service.DomainServiceTestsConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link ProductCategoryDomainService} class
 *
 * @author Anton Borovyk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DomainServiceTestsConfig.class)
public class ProductCategoryDomainServiceTest {

    private static final ProductCategory dummyCategory = new ProductCategory(12345, "dummyCategory");

    @Autowired
    private ProductCategoryDomainService categoryService;

    @Autowired
    private IProductCategoryRepo categoryRepo;

    @Test
    public void getCategoriesTest() throws Exception {
        List<ProductCategory> expected = Collections.singletonList(dummyCategory);

        when(categoryRepo.getCategories()).thenReturn(expected);

        List<ProductCategory> actual = categoryService.getCategories();
        assertEquals(expected, actual);

        verify(categoryRepo, times(1)).getCategories();
    }

    @Test
    public void addCategoryTest() throws Exception {
        when(categoryRepo.addCategory(dummyCategory)).thenReturn(true);
        assertTrue(categoryService.addCategory(dummyCategory));
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
        when(categoryRepo.deleteCategory(dummyCategory)).thenReturn(true);
        assertTrue(categoryService.deleteCategory(dummyCategory));
        verify(categoryRepo, times(1)).deleteCategory(dummyCategory);
    }

}