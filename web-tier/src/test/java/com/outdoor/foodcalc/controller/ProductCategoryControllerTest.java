package com.outdoor.foodcalc.controller;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import com.outdoor.foodcalc.service.ProductCategoryService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductCategoryController} class
 *
 * @author Anton Borovyk.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ServiceTestsConfig.class)
public class ProductCategoryControllerTest {

    @Autowired
    private ProductCategoryController controller;

    private ProductCategoryService service;

    @Before
    public void setUp() throws Exception {
        service = mock(ProductCategoryService.class);
        ReflectionTestUtils.setField(controller, "service", service);
    }

    @Test
    public void allProducts() throws Exception {
        SimpleProductCategory category = new SimpleProductCategory();
        category.id = 12345;
        category.name = "Test";
        ModelMap model = new ModelMap();

        when(service.getCategories()).thenReturn(Collections.singletonList(category));

        assertEquals("categories", controller.allCategories(model));
        assertNotNull(model.get("categories"));

        List actualList = (List) model.get("categories");
        assertEquals(1, actualList.size());
        SimpleProductCategory actual = (SimpleProductCategory) actualList.get(0);
        assertEquals(12345, actual.id);
        assertEquals("Test", actual.name);

        verify(service, times(1)).getCategories();
    }

}