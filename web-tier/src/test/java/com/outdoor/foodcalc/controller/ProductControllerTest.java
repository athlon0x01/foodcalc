package com.outdoor.foodcalc.controller;

import com.outdoor.foodcalc.model.CategoryWithProducts;
import com.outdoor.foodcalc.service.ProductService;
import com.outdoor.foodcalc.service.ServiceTestsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMap;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductController} class
 *
 * @author Anton Borovyk.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceTestsConfig.class)
public class ProductControllerTest {

    @Autowired
    private ProductController controller;

    private ProductService service;

    @Before
    public void setUp() throws Exception {
        service = mock(ProductService.class);
        ReflectionTestUtils.setField(controller, "productService", service);
    }

    @Test
    public void allProducts() throws Exception {
        CategoryWithProducts category = new CategoryWithProducts();
        category.id = 12345;
        category.name = "Test";
        category.products = Collections.emptyList();
        ModelMap model = new ModelMap();

        when(service.getAllProducts()).thenReturn(Collections.singletonList(category));

        assertEquals("products", controller.allProducts(model));
        assertNotNull(model.get("categories"));

        List actualList = (List) model.get("categories");
        assertEquals(1, actualList.size());
        CategoryWithProducts actual = (CategoryWithProducts) actualList.get(0);
        assertEquals(12345, actual.id);
        assertEquals("Test", actual.name);
        assertTrue(actual.products.isEmpty());


        verify(service, times(1)).getAllProducts();
    }

}