package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.dish.SimpleDishCategory;
import com.outdoor.foodcalc.service.DishCategoryService;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DishCategoryEndpointTest extends ApiUnitTest {

    private static final long CATEGORY_ID = 12345;
    private static final String CATEGORY_NAME = "Test category";

    private SimpleDishCategory dummyCategory;

    @MockBean
    private DishCategoryService service;

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
        dummyCategory = new SimpleDishCategory();
        dummyCategory.id = CATEGORY_ID;
        dummyCategory.name = CATEGORY_NAME;
    }

    @Test
    public void getCategoriesTest() throws Exception {
        List<SimpleDishCategory> expected = Collections.singletonList(dummyCategory);

        when(service.getDishCategories()).thenReturn(expected);

        MvcResult mvcResult = get("/dish-categories")
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        SimpleDishCategory[] actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleDishCategory[].class);
        assertEquals(expected, Arrays.asList(actual));

        verify(service).getDishCategories();
    }

    @Test
    public void getCategoryTest() throws Exception {
        SimpleDishCategory expected = dummyCategory;

        when(service.getDishCategory(CATEGORY_ID)).thenReturn(expected);

        MvcResult mvcResult = get("/dish-categories/" + CATEGORY_ID).andReturn();

        SimpleDishCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleDishCategory.class);
        assertEquals(expected, actual);

        verify(service).getDishCategory(CATEGORY_ID);
    }

    @Test
    public void getCategoryNotFoundTest() throws Exception {
        String message = "Dish Category wasn't found";
        when(service.getDishCategory(CATEGORY_ID)).thenThrow(new NotFoundException(message));

        get404("/dish-categories/" + CATEGORY_ID)
                .andExpect(jsonPath("$", is(message)));

        verify(service).getDishCategory(CATEGORY_ID);
    }

    @Test
    public void addCategoryTest() throws Exception {
        SimpleDishCategory category = new SimpleDishCategory();
        category.name = CATEGORY_NAME;

        when(service.addDishCategory(category)).thenReturn(dummyCategory);  //?????

        MvcResult mvcResult = post("/dish-categories", category)
                .andReturn();

        SimpleDishCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleDishCategory.class);
        assertEquals(dummyCategory, actual);

        verify(service).addDishCategory(category);
    }

    @Test
    public void addCategoryValidationErrorTest() throws Exception {
            SimpleDishCategory category = new SimpleDishCategory();

            post400("/dish-categories/", category);

            verify(service, never()).addDishCategory(category);
    }

    @Test
    public void updateCategoryTest() throws Exception {
        when(service.updateDishCategory(dummyCategory)).thenReturn(true);

        MvcResult mvcResult = put("/dish-categories/" + CATEGORY_ID, dummyCategory).
                andReturn();

        SimpleDishCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), SimpleDishCategory.class);
        assertEquals(dummyCategory, actual);

        verify(service).updateDishCategory(dummyCategory);
    }

    @Test
    public void updateCategoryValidationTest() throws Exception {
        String message = "Path variable Id = 55 doesn't match with request body Id = " + CATEGORY_ID;

        put400("/dish-categories/55", dummyCategory).
                andExpect(jsonPath("&", is(message)));

        verify(service, never()).updateDishCategory(dummyCategory);
    }

    @Test
    public void updateCategoryInternalErrorsTest() throws Exception {
        String message = "Dish category failed to update";
        when(service.updateDishCategory(dummyCategory)).thenReturn(false);

        putJson("/dish-categories/" + CATEGORY_ID, dummyCategory).
                andExpect(status().isInternalServerError()).
                andExpect(jsonPath("$", is(message)));

        verify(service).updateDishCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        when(service.deleteDishCategory(CATEGORY_ID)).thenReturn(true);

        delete("/dish-categories/" + CATEGORY_ID);

        verify(service).deleteDishCategory(CATEGORY_ID);
    }

    @Test
    public void deleteCategoryNotFoundTest() throws Exception {
        String message = "Dish category doesn't exist";
        when(service.deleteDishCategory(CATEGORY_ID)).thenThrow(new NotFoundException(message));

        delete404("/dish-categories/" + CATEGORY_ID).andExpect(jsonPath("$", is(message)));

        verify(service).deleteDishCategory(CATEGORY_ID);

    }

    @Test
    public void deleteInternalErrorTest() throws Exception {
        String message = "Dish category failed to delete";
        when(service.deleteDishCategory(CATEGORY_ID)).thenReturn(false);

        deleteJson("/dish-categories/" + CATEGORY_ID).
                andExpect(status().isInternalServerError()).
                andExpect(jsonPath("$", is(message)));

        verify(service).deleteDishCategory(CATEGORY_ID);
    }

}
