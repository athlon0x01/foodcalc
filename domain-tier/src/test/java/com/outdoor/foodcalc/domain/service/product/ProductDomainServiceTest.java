package com.outdoor.foodcalc.domain.service.product;

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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductDomainService} class
 *
 * @author Anton Borovyk
 */
public class ProductDomainServiceTest {

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
        Product dummyProduct = new Product(12345, "dummyProduct", "",
            new ProductCategory(11123, "dummyCategory"),
            1.1f, 3, 4.5f, 7, 11);
        List<Product> expected = Collections.singletonList(dummyProduct);

        when(productRepo.getAllProducts()).thenReturn(expected);

        List<Product> actual = productService.getAllProducts();
        assertEquals(expected, actual);

        verify(productRepo).getAllProducts();
    }
}
