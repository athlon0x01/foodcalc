package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.endpoint.impl.ProductCategoryEndpoint;
import com.outdoor.foodcalc.model.product.ProductCategoryView;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit tests for {@link ProductCategoryEndpoint} class
 *
 * @author Anton Borovyk.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ProductCategoryEndpointTest extends ApiUnitTest {

    private static final long CATEGORY_ID = 12345;
    private static final String CATEGORY_NAME = "Test category";
    private static final ProductCategoryView dummyCategory = ProductCategoryView.builder()
            .id(CATEGORY_ID)
            .name(CATEGORY_NAME)
            .build();

    @MockBean
    private ProductCategoryService service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Override
    public void setMapper(ObjectMapper mapper) {
        super.setMapper(mapper);
    }

    @BeforeEach
    public void setUp() {
        setMockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    public void getCategoriesTest() throws Exception {
        List<ProductCategoryView> expected = Collections.singletonList(dummyCategory);

        when(service.getCategories()).thenReturn(expected);

        MvcResult mvcResult = get("/product-categories")
            .andExpect(jsonPath("$", hasSize(1)))
            .andReturn();

        ProductCategoryView[] actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductCategoryView[].class);
        assertEquals(expected, Arrays.asList(actual));

        verify(service).getCategories();
    }

    @Test
    public void getCategoryTest() throws Exception {
        ProductCategoryView expected = dummyCategory;

        when(service.getCategory(CATEGORY_ID)).thenReturn(expected);

        MvcResult mvcResult = get("/product-categories/" + CATEGORY_ID)
            .andReturn();

        ProductCategoryView actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductCategoryView.class);
        assertEquals(expected, actual);

        verify(service).getCategory(CATEGORY_ID);
    }

    @Test
    public void getCategoryNotFoundTest() throws Exception {
        String message = "Product category wasn't found";
        when(service.getCategory(CATEGORY_ID)).thenThrow(new NotFoundException(message));

        get404("/product-categories/" + CATEGORY_ID)
                .andExpect(jsonPath("$", is(message)));

        verify(service).getCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() throws Exception {
        ProductCategoryView category = ProductCategoryView.builder()
                .name(CATEGORY_NAME)
                .build();

        when(service.addCategory(CATEGORY_NAME)).thenReturn(dummyCategory);

        MvcResult mvcResult = post("/product-categories", category)
            .andReturn();

        ProductCategoryView actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), ProductCategoryView.class);
        assertEquals(dummyCategory, actual);

        verify(service).addCategory(CATEGORY_NAME);
    }

    @Test
    public void addCategoryValidationErrorTest() throws Exception {
        ProductCategoryView category = ProductCategoryView.builder().build();

        post400("/product-categories", category);

        verify(service, never()).addCategory(CATEGORY_NAME);
    }

    @Test
    public void updateCategoryTest() throws Exception {
        doNothing().when(service).updateCategory(dummyCategory);

        put("/product-categories/" + CATEGORY_ID, dummyCategory)
            .andReturn();

        verify(service).updateCategory(dummyCategory);
    }

    @Test
    public void updateCategoryNotFoundTest() throws Exception {
        String message = "Product category with id=" + CATEGORY_ID + " doesn't exist";
        doThrow(new NotFoundException(message)).when(service).updateCategory(dummyCategory);

        put404("/product-categories/" + CATEGORY_ID, dummyCategory)
                .andExpect(jsonPath("$", is(message)));

        verify(service).updateCategory(dummyCategory);
    }

    @Test
    public void updateCategoryValidationTest() throws Exception {
        String message = "Path variable Id = 55 doesn't match with request body Id = " + CATEGORY_ID;

        put400("/product-categories/55", dummyCategory)
                .andExpect(jsonPath("$", is(message)));

        verify(service, never()).updateCategory(dummyCategory);
    }

    @Test
    public void updateCategoryInternalErrorTest() throws Exception {
        String message = "Failed to update product category with id=" + CATEGORY_ID;
        doThrow(new FoodcalcDomainException(message)).when(service).updateCategory(dummyCategory);


        putJson("/product-categories/" + CATEGORY_ID, dummyCategory).
                andExpect(status().is5xxServerError()).
                andExpect(jsonPath("$", is(message)));

        verify(service).updateCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        doNothing().when(service).deleteCategory(CATEGORY_ID);

        delete("/product-categories/" + CATEGORY_ID);

        verify(service).deleteCategory(CATEGORY_ID);
    }

    @Test
    public void deleteCategoryNotFoundTest() throws Exception {
        String message = "Product category with id=" + CATEGORY_ID + " doesn't exist";
        doThrow(new NotFoundException(message)).when(service).deleteCategory(CATEGORY_ID);

        delete404("/product-categories/" + CATEGORY_ID).andExpect(jsonPath("$", is(message)));

        verify(service).deleteCategory(CATEGORY_ID);
    }

    @Test
    public void deleteInternalErrorTest() throws Exception {
        String message = "Failed to delete product category with id=" + CATEGORY_ID;
        doThrow(new FoodcalcDomainException(message))
                .when(service).deleteCategory(CATEGORY_ID);

        deleteJson("/product-categories/" + CATEGORY_ID).
                andExpect(status().isInternalServerError()).
                andExpect(jsonPath("$", is(message)));

        verify(service).deleteCategory(CATEGORY_ID);
    }
}
