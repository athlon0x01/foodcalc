package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.dish.DishCategory;
import com.outdoor.foodcalc.service.DishCategoryService;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class DishCategoryEndpointTest extends ApiUnitTest {

    private static final long CATEGORY_ID = 12345;
    private static final String CATEGORY_NAME = "Test category";
    private static final DishCategory dummyCategory = DishCategory.builder()
            .id(CATEGORY_ID)
            .name(CATEGORY_NAME)
            .build();

    @MockBean
    private DishCategoryService service;

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
        List<DishCategory> expected = Collections.singletonList(dummyCategory);

        when(service.getDishCategories()).thenReturn(expected);

        MvcResult mvcResult = get("/dish-categories")
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        DishCategory[] actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), DishCategory[].class);
        assertEquals(expected, Arrays.asList(actual));

        verify(service).getDishCategories();
    }

    @Test
    public void getCategoryTest() throws Exception {
        DishCategory expected = dummyCategory;

        when(service.getDishCategory(CATEGORY_ID)).thenReturn(expected);

        MvcResult mvcResult = get("/dish-categories/" + CATEGORY_ID).andReturn();

        DishCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), DishCategory.class);
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
        DishCategory category = DishCategory.builder()
                .name(CATEGORY_NAME)
                .build();

        when(service.addDishCategory(category.getName())).thenReturn(dummyCategory);

        MvcResult mvcResult = post("/dish-categories", category)
                .andReturn();

        DishCategory actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), DishCategory.class);
        assertEquals(dummyCategory, actual);

        verify(service).addDishCategory(category.getName());
    }

    @Test
    public void addCategoryValidationErrorTest() throws Exception {
            DishCategory category = DishCategory.builder().build();

            post400("/dish-categories/", category);

            verify(service, never()).addDishCategory(category.getName());
    }

    @Test
    public void updateCategoryTest() throws Exception {
        doNothing().when(service).updateDishCategory(dummyCategory);

        put("/dish-categories/" + CATEGORY_ID, dummyCategory).
                andReturn();

        verify(service).updateDishCategory(dummyCategory);
    }

    @Test
    public void updateCategoryValidationTest() throws Exception {
        String message = "Path variable Id = 55 doesn't match with request body Id = " + CATEGORY_ID;

        put400("/dish-categories/55", dummyCategory).
                andExpect(jsonPath("$", is(message)));

        verify(service, never()).updateDishCategory(dummyCategory);
    }

    @Test
    public void updateCategoryNotFoundTest() throws Exception {
        String message = "Dish category with id=" + CATEGORY_ID + " doesn't exist";
        doThrow(new NotFoundException(message)).when(service).updateDishCategory(dummyCategory);


        put404("/dish-categories/" + CATEGORY_ID, dummyCategory).
                andExpect(status().isNotFound()).
                andExpect(jsonPath("$", is(message)));

        verify(service).updateDishCategory(dummyCategory);
    }

    @Test
    public void updateCategoryInternalErrorsTest() throws Exception {
        String message = "Failed to update dish category with id=" + CATEGORY_ID;
        doThrow(new FoodcalcDomainException(message)).when(service).updateDishCategory(dummyCategory);


        putJson("/dish-categories/" + CATEGORY_ID, dummyCategory).
                andExpect(status().is5xxServerError()).
                andExpect(jsonPath("$", is(message)));

        verify(service).updateDishCategory(dummyCategory);
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        doNothing().when(service).deleteDishCategory(CATEGORY_ID);

        delete("/dish-categories/" + CATEGORY_ID);

        verify(service).deleteDishCategory(CATEGORY_ID);
    }

    @Test
    public void deleteCategoryNotFoundTest() throws Exception {
        String message = "Dish category with id=" + CATEGORY_ID + " doesn't exist";
        doThrow(new NotFoundException(message))
                .when(service).deleteDishCategory(CATEGORY_ID);

        delete404("/dish-categories/" + CATEGORY_ID).andExpect(jsonPath("$", is(message)));

        verify(service).deleteDishCategory(CATEGORY_ID);
    }

    @Test
    public void deleteInternalErrorTest() throws Exception {
        String message = "Failed to delete dish category with id=" + CATEGORY_ID;
        doThrow(new FoodcalcDomainException(message))
                .when(service).deleteDishCategory(CATEGORY_ID);

        deleteJson("/dish-categories/" + CATEGORY_ID).
                andExpect(status().isInternalServerError()).
                andExpect(jsonPath("$", is(message)));

        verify(service).deleteDishCategory(CATEGORY_ID);
    }
}
