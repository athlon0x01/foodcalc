package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import com.outdoor.foodcalc.domain.service.DomainServiceTestsConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductDomainService} class
 *
 * @author Anton Borovyk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DomainServiceTestsConfig.class)
public class ProductDomainServiceTest {

    @Autowired
    private ProductDomainService productService;

    @Autowired
    private IProductRepo productRepo;

    @Test
    public void getAllProductsTest() {
        Product dummyProduct = new Product(12345, "dummyProduct",
            new ProductCategory(11123, "dummyCategory"),
            1.1f, 3, 4.5f, 7, 11);
        List<Product> expected = Collections.singletonList(dummyProduct);

        when(productRepo.getAllProducts()).thenReturn(expected);

        List<Product> actual = productService.getAllProducts();
        assertEquals(expected, actual);

        verify(productRepo, times(1)).getAllProducts();
    }
}
