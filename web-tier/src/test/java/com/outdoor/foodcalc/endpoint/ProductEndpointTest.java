package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.endpoint.impl.ProductEndpoint;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.SimpleProduct;
import com.outdoor.foodcalc.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * JUnit tests for {@link ProductEndpoint} class
 *
 * @author Anton Borovyk.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductEndpointTest extends ApiUnitTest {

    @MockBean
    private ProductService service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Override
    public void setMapper(ObjectMapper mapper) {
        super.setMapper(mapper);
    }

    @Before
    public void setUp() {
        setMockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    public void allProducts() throws Exception {
        CategoryWithProducts category = new CategoryWithProducts();
        SimpleProduct product = new SimpleProduct();
        product.id = 357891;
        product.name = "Sugar";
        category.id = 12345;
        category.name = "Sweets";
        category.products = Collections.singletonList(product);
        List<CategoryWithProducts> products = Collections.singletonList(category);

        when(service.getAllProducts()).thenReturn(products);

        get("/products")
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(12345)))
            .andExpect(jsonPath("$[0].name", is("Sweets")))
            .andExpect(jsonPath("$[0].products", hasSize(1)))
            .andExpect(jsonPath("$[0].products[0].id", is(357891)))
            .andExpect(jsonPath("$[0].products[0].name", is("Sugar")));

        verify(service).getAllProducts();
    }

}