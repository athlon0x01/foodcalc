package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
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
 * JUnit tests for {@link ProductDomainService} class
 *
 * @author Anton Borovyk
 */
@ExtendWith(MockitoExtension.class)
public class ProductDomainServiceTest {

    private static final Long PRODUCT_ID = 54321L;
    private static final long CATEGORY_ID = 12345L;
    private static final ProductCategory dummyCategory =  new ProductCategory(CATEGORY_ID, "dummuCategory");

    private static final Product dummyProduct = Product.builder().productId(PRODUCT_ID).name("dummyProduct")
            .category(dummyCategory).calorific(1.1f).proteins(2.2f).fats(3.3f).carbs(4.4f).defaultWeight(10).build();


    @InjectMocks
    private ProductDomainService productService;

    @Mock
    private IProductRepo productRepo;

    @Mock
    private ProductCategoryDomainService productCategoryService;

    @Test
    public void getAllProductsTest() {
        List<Product> expected = Collections.singletonList(dummyProduct);

        when(productRepo.getAllProducts()).thenReturn(expected);

        List<Product> actual = productService.getAllProducts();
        assertEquals(expected, actual);

        verify(productRepo).getAllProducts();
    }

    @Test
    public void getProductTest() {
        Optional<Product> expected = Optional.of(dummyProduct);
        when(productRepo.getProduct(PRODUCT_ID)).thenReturn(expected);

        Optional<Product> actual = productService.getProduct(PRODUCT_ID);
        assertEquals(expected, actual);

        verify(productRepo).getProduct(PRODUCT_ID);
    }

    @Test
    public void addProductTest() {
        Product productToAdd = Product.builder().productId(-1).name("dummyProduct")
                .category(dummyCategory).calorific(1.1f).proteins(2.2f).fats(3.3f).carbs(4.4f).defaultWeight(10).build();
        when(productCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyCategory));
        when(productRepo.addProduct(productToAdd)).thenReturn(PRODUCT_ID);

        assertEquals(dummyProduct, productService.addProduct(productToAdd));

        verify(productCategoryService).getCategory(CATEGORY_ID);
        verify(productRepo).addProduct(productToAdd);
    }

    @Test
    public void updateProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyCategory));
        when(productRepo.updateProduct(dummyProduct)).thenReturn(true);

        productService.updateProduct(dummyProduct);

        verify(productRepo).existsProduct(PRODUCT_ID);
        verify(productCategoryService).getCategory(CATEGORY_ID);
        verify(productRepo).updateProduct(dummyProduct);
    }

    @Test
    public void updateNotExistingProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> {
            productService.updateProduct(dummyProduct);
        });
    }

    @Test
    public void updateProductFailTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productCategoryService.getCategory(CATEGORY_ID)).thenReturn(Optional.of(dummyCategory));
        when(productRepo.updateProduct(dummyProduct)).thenReturn(false);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            productService.updateProduct(dummyProduct);
        });
    }

    @Test
    public void deleteProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productRepo.deleteProduct(PRODUCT_ID)).thenReturn(true);

        productService.deleteProduct(PRODUCT_ID);

        verify(productRepo).existsProduct(PRODUCT_ID);
        verify(productRepo).deleteProduct(PRODUCT_ID);
    }

    @Test
    public void deleteNotExistingProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> {
            productService.deleteProduct(PRODUCT_ID);
        });
    }

    @Test
    public void deleteProductFailTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productRepo.deleteProduct(PRODUCT_ID)).thenReturn(false);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            productService.deleteProduct(PRODUCT_ID);
        });
    }
}
