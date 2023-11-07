package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.endpoint.impl.ProductEndpoint;
import com.outdoor.foodcalc.model.product.CategoryWithProducts;
import com.outdoor.foodcalc.model.product.ProductView;
import com.outdoor.foodcalc.service.ProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit tests for {@link ProductEndpoint} class
 *
 * @author Anton Borovyk.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductEndpointTest extends ApiUnitTest {

    public static final long PRODUCT_ID = 357891;
    private static final ProductView PRODUCT_VIEW = ProductView.builder()
            .id(PRODUCT_ID).name("sugar").categoryId(12345).build();

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
    public void getAllProductsTest() throws Exception {
        CategoryWithProducts category = CategoryWithProducts.builder()
                .id(12345).name("Sweets").products(Collections.singletonList(PRODUCT_VIEW)).build();
        List<CategoryWithProducts> products = Collections.singletonList(category);

        when(service.getAllProducts()).thenReturn(products);

        get("/products")
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(12345)))
            .andExpect(jsonPath("$[0].name", is("Sweets")))
            .andExpect(jsonPath("$[0].products", hasSize(1)))
            .andExpect(jsonPath("$[0].products[0].id", is(357891)))
            .andExpect(jsonPath("$[0].products[0].name", is("sugar")))
            .andExpect(jsonPath("$[0].products[0].categoryId", is(12345)));

        verify(service).getAllProducts();
    }

    @Test
    public void getProductTest() throws Exception {
        ProductView expectedProduct = PRODUCT_VIEW;
        when(service.getProduct(PRODUCT_ID)).thenReturn(expectedProduct);

        MvcResult mvcResult = get("/products/" + PRODUCT_ID)
                .andReturn();

        ProductView actualProduct = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductView.class);
        assertEquals(expectedProduct, actualProduct);

        verify(service).getProduct(PRODUCT_ID);
    }

    @Test
    public void getProductNotFoundTest() throws Exception {
        String message = "Product not found";
        when(service.getProduct(PRODUCT_ID)).thenThrow(new NotFoundException(message));

        get404("/products/" + PRODUCT_ID).andExpect(jsonPath("$", is(message)));

        verify(service).getProduct(PRODUCT_ID);
    }

    @Test
    public void addProductTest() throws Exception {
        ProductView productToAdd = ProductView.builder().name("Sugar").categoryId(12345).build();
        ProductView expectedProduct = PRODUCT_VIEW;
        when(service.addProduct(productToAdd)).thenReturn(expectedProduct);

        MvcResult mvcResult = post("/products", productToAdd).andReturn();

        ProductView actualProduct = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductView.class);
        assertEquals(expectedProduct, actualProduct);

        verify(service).addProduct(productToAdd);
    }

    @Test
    public void addProductValidationErrorTest() throws Exception {
        ProductView productToAdd = ProductView.builder().build();

        post400("/products", productToAdd);

        verify(service, never()).addProduct(productToAdd);
    }

    @Test
    public void updateProductTest() throws Exception {
        ProductView productToUpdate = PRODUCT_VIEW;
        when(service.updateProduct(productToUpdate)).thenReturn(true);

        MvcResult mvcResult = put("/products/" + PRODUCT_ID, productToUpdate).andReturn();
        ProductView actualProduct = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductView.class);
        assertEquals(productToUpdate, actualProduct);

        verify(service).updateProduct(productToUpdate);
    }

    @Test
    public void updateProductValidationTest() throws Exception {
        String message = "Path variable Id = 55 doesn't match with request body Id = " + PRODUCT_ID;
        ProductView productToUpdate = PRODUCT_VIEW;

        put400("/products/55", productToUpdate)
                .andExpect(jsonPath("$", is(message)));

        verify(service, never()).updateProduct(productToUpdate);
    }

    @Test
    public void updateProductInternalErrorTest() throws Exception {
        String message = "Product failed to update";
        ProductView productToUpdate = PRODUCT_VIEW;
        when(service.updateProduct(productToUpdate)).thenReturn(false);

        putJson("/products/" + productToUpdate.getId(), productToUpdate)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).updateProduct(productToUpdate);
    }

    @Test
    public void updateProductNotFoundTest() throws Exception {
        String message = "Product doesn't exist";
        ProductView productToUpdate = PRODUCT_VIEW;
        when(service.updateProduct(productToUpdate)).thenThrow(new NotFoundException(message));

        put404("/products/" + productToUpdate.getId(), productToUpdate)
                .andExpect(jsonPath("$", is(message)));

        verify(service).updateProduct(productToUpdate);
    }

    @Test
    public void deleteProductTest() throws Exception {
        when(service.deleteProduct(PRODUCT_ID)).thenReturn(true);

        delete("/products/" + PRODUCT_ID);

        verify(service).deleteProduct(PRODUCT_ID);
    }

    @Test
    public void deleteProductNotFoundTest() throws Exception {
        String message = "Product doesn't exist";
        when(service.deleteProduct(PRODUCT_ID)).thenThrow(new NotFoundException(message));

        delete404("/products/" + PRODUCT_ID)
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteProduct(PRODUCT_ID);
    }

    @Test
    public void deleteProductInternalErrorTest() throws Exception {
        String message = "Product failed to delete";
        when(service.deleteProduct(PRODUCT_ID)).thenReturn(false);

        deleteJson("/products/" + PRODUCT_ID)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteProduct(PRODUCT_ID);
    }
}
