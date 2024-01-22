package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductCategoryDomainService} class
 *
 * @author Anton Borovyk
 */
@ExtendWith(MockitoExtension.class)
public class ProductCategoryDomainServiceTest {

    private static final long CATEGORY_ID = 12345;
    private static final ProductCategory dummyCategory = new ProductCategory(CATEGORY_ID, "dummyCategory");

    @InjectMocks
    private ProductCategoryDomainService categoryService;

    @Mock
    private IProductCategoryRepo categoryRepo;

    @Mock
    private IProductRepo productRepo;

    @Test
    public void getCategoriesTest() {
        List<ProductCategory> expected = Collections.singletonList(dummyCategory);

        when(categoryRepo.getCategories()).thenReturn(expected);

        List<ProductCategory> actual = categoryService.getCategories();
        assertEquals(expected, actual);

        verify(categoryRepo).getCategories();
    }

    @Test
    public void getCategoryTest() {
        Optional<ProductCategory> expected = Optional.of(dummyCategory);

        when(categoryRepo.getCategory(CATEGORY_ID)).thenReturn(expected);

        Optional<ProductCategory> actual = categoryService.getCategory(CATEGORY_ID);
        assertEquals(expected, actual);

        verify(categoryRepo).getCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() {
        ProductCategory categoryToAdd = new ProductCategory(-1, "dummyCategory");
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

    @Test
    public void updateNotExistingCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.updateCategory(dummyCategory);
        });
    }

    @Test
    public void updateCategoryFailTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(true);
        when(categoryRepo.updateCategory(dummyCategory)).thenReturn(false);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            categoryService.updateCategory(dummyCategory);
        });
    }


    @Test
    public void deleteCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(true);
        when(productRepo.countProductsInCategory(CATEGORY_ID)).thenReturn(0L);
        when(categoryRepo.deleteCategory(CATEGORY_ID)).thenReturn(true);

        categoryService.deleteCategory(CATEGORY_ID);

        verify(categoryRepo).exist(CATEGORY_ID);
        verify(productRepo).countProductsInCategory(CATEGORY_ID);
        verify(categoryRepo).deleteCategory(CATEGORY_ID);
    }

    @Test
    public void deleteNotExistingCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.deleteCategory(CATEGORY_ID);
        });
    }

    @Test
    public void deleteNotEmptyCategoryTest() {
        when(categoryRepo.exist(CATEGORY_ID)).thenReturn(true);
        when(productRepo.countProductsInCategory(CATEGORY_ID)).thenReturn(1L);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(CATEGORY_ID);
        });
    }

}