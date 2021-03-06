package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.endpoint.impl.ProductCategoryEndpoint;
import com.outdoor.foodcalc.model.product.SimpleProductCategory;
import com.outdoor.foodcalc.service.ProductCategoryService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * JUnit tests for {@link ProductCategoryEndpoint} class
 *
 * @author Anton Borovyk.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductCategoryEndpointTest extends ApiUnitTest {

    private static final long CATEGORY_ID = 12345;
    private static final String CATEGORY_NAME = "Test category";

    private SimpleProductCategory dummyCategory;

    @MockBean
    private ProductCategoryService service;

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
        dummyCategory = new SimpleProductCategory();
        dummyCategory.id = CATEGORY_ID;
        dummyCategory.name = CATEGORY_NAME;
    }

    @Test
    public void getCategoriesTest() throws Exception {
        List<SimpleProductCategory> expected = Collections.singletonList(dummyCategory);

        when(service.getCategories()).thenReturn(expected);

        MvcResult mvcResult = get("/product-categories")
            .andExpect(jsonPath("$", hasSize(1)))
            .andReturn();

        SimpleProductCategory[] actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleProductCategory[].class);
        assertEquals(expected, Arrays.asList(actual));

        verify(service).getCategories();
    }

    @Test
    public void getCategoryTest() throws Exception {
        SimpleProductCategory expected = dummyCategory;

        when(service.getCategory(CATEGORY_ID)).thenReturn(expected);

        MvcResult mvcResult = get("/product-categories/" + CATEGORY_ID)
            .andReturn();

        SimpleProductCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleProductCategory.class);
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
        SimpleProductCategory category = new SimpleProductCategory();
        category.name = CATEGORY_NAME;

        when(service.addCategory(CATEGORY_NAME)).thenReturn(dummyCategory);

        MvcResult mvcResult = post("/product-categories", category)
            .andReturn();

        SimpleProductCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleProductCategory.class);
        assertEquals(dummyCategory, actual);

        verify(service).addCategory(CATEGORY_NAME);
    }

    @Test
    public void addCategoryValidationErrorTest() throws Exception {
        SimpleProductCategory category = new SimpleProductCategory();

        post400("/product-categories", category);

        verify(service, never()).addCategory(CATEGORY_NAME);
    }

    @Test
    public void updateCategoryTest() throws Exception {
        when(service.updateCategory(dummyCategory)).thenReturn(true);

        MvcResult mvcResult = put("/product-categories/" + CATEGORY_ID, dummyCategory)
            .andReturn();

        SimpleProductCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleProductCategory.class);
        assertEquals(dummyCategory, actual);

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
        String message = "Product category failed to update";
        when(service.updateCategory(dummyCategory)).thenReturn(false);

        putJson("/product-categories/" + CATEGORY_ID, dummyCategory)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).updateCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        when(service.deleteCategory(CATEGORY_ID)).thenReturn(true);

        delete("/product-categories/" + CATEGORY_ID);

        verify(service).deleteCategory(CATEGORY_ID);
    }

    @Test
    public void deleteCategoryNotFoundTest() throws Exception {
        String message = "Product category doesn't exist";
        when(service.deleteCategory(CATEGORY_ID)).thenThrow(new NotFoundException(message));

        delete404("/product-categories/" + CATEGORY_ID)
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteCategory(CATEGORY_ID);
    }


    @Test
    public void deleteInternalErrorTest() throws Exception {
        String message = "Product category failed to delete";
        when(service.deleteCategory(CATEGORY_ID)).thenReturn(false);

        deleteJson("/product-categories/" + CATEGORY_ID)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteCategory(CATEGORY_ID);
    }

}