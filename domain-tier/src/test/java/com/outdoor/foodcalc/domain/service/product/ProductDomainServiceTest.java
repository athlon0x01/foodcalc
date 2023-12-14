package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductDomainService} class
 *
 * @author Anton Borovyk
 */
public class ProductDomainServiceTest {

    private static final Long PRODUCT_ID = 54321L;

    private static final ProductCategory dummyCategory =  new ProductCategory(12345L, "dummuCategory");

    private static final Product dummyProduct = Product.builder().productId(PRODUCT_ID).name("dummyProduct")
            .description("dummyDescr").category(dummyCategory).calorific(1.1f)
            .proteins(2.2f).fats(3.3f).carbs(4.4f).defaultWeight(10).build();


    @InjectMocks
    private ProductDomainService productService;

    @Mock
    private IProductRepo productRepo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

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
                .description("dummyDescr").category(dummyCategory).calorific(1.1f)
                .proteins(2.2f).fats(3.3f).carbs(4.4f).defaultWeight(10).build();
        when(productRepo.addProduct(productToAdd)).thenReturn(PRODUCT_ID);

        assertEquals(dummyProduct, productService.addProduct(productToAdd));

        verify(productRepo).addProduct(productToAdd);
    }

    @Test
    public void updateProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productRepo.updateProduct(dummyProduct)).thenReturn(true);

        productService.updateProduct(dummyProduct);

        verify(productRepo).existsProduct(PRODUCT_ID);
        verify(productRepo).updateProduct(dummyProduct);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExistingProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(false);

        productService.updateProduct(dummyProduct);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void updateProductFailTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productRepo.updateProduct(dummyProduct)).thenReturn(false);

        productService.updateProduct(dummyProduct);
    }

    @Test
    public void deleteProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productRepo.deleteProduct(PRODUCT_ID)).thenReturn(true);

        productService.deleteProduct(PRODUCT_ID);

        verify(productRepo).existsProduct(PRODUCT_ID);
        verify(productRepo).deleteProduct(PRODUCT_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExistingProductTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(false);

        productService.deleteProduct(PRODUCT_ID);
    }

    @Test(expected = FoodcalcDomainException.class)
    public void deleteProductFailTest() {
        when(productRepo.existsProduct(PRODUCT_ID)).thenReturn(true);
        when(productRepo.deleteProduct(PRODUCT_ID)).thenReturn(false);

        productService.deleteProduct(PRODUCT_ID);
    }
}
