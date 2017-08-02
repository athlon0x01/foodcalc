package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.CategoryWithProducts;
import com.outdoor.foodcalc.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductEndpoint} class
 *
 * @author Anton Borovyk.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductEndpointTest {

    private ProductEndpoint endpoint;

    @Mock
    private ProductService service;

    @Before
    public void setUp() throws Exception {
        endpoint = new ProductEndpoint(service);
    }

    @Test
    public void allProducts() throws Exception {
        CategoryWithProducts category = new CategoryWithProducts();
        category.id = 12345;
        category.name = "Test";
        category.products = Collections.emptyList();
        List<CategoryWithProducts> products = Collections.singletonList(category);

        when(service.getAllProducts()).thenReturn(products);

        assertEquals(products, endpoint.allProducts());

        verify(service, times(1)).getAllProducts();
    }

}