package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.meal.MealType;
import com.outdoor.foodcalc.service.MealTypesService;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class MealTypesEndpointTest extends ApiUnitTest{

    private static final long MEAL_TYPE_ID = 12345;
    private static final String MEAL_TYPE_NAME = "Test meal type";

    private MealType dummyMealType;

    @MockBean
    private MealTypesService service;

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
        dummyMealType = new MealType();
        dummyMealType.id = MEAL_TYPE_ID;
        dummyMealType.name = MEAL_TYPE_NAME;
    }

    @Test
    public void getMealTypesTest() throws Exception {
        List<MealType> expected = Collections.singletonList(dummyMealType);

        when(service.getMealTypes()).thenReturn(expected);

        MvcResult mvcResult = get("/meal-types")
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        MealType[] actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), MealType[].class);
        assertEquals(expected, Arrays.asList(actual));

        verify(service).getMealTypes();
    }

    @Test
    public void getMealTypeTest() throws Exception {
        MealType expected = dummyMealType;

        when(service.getMealType(dummyMealType.id)).thenReturn(expected);

        MvcResult mvcResult = get("/meal-types/" + MEAL_TYPE_ID).andReturn();

        MealType actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), MealType.class);
        assertEquals(expected, actual);

        verify(service).getMealType(MEAL_TYPE_ID);
    }

    @Test
    public void getMealTypeNotFoundTest() throws Exception {
        String message = "Meal type wasn't found";

        when(service.getMealType(MEAL_TYPE_ID)).thenThrow(new NotFoundException(message));

        get404("/meal-types/" + MEAL_TYPE_ID).andExpect(jsonPath("$", is(message)));

        verify(service).getMealType(MEAL_TYPE_ID);
    }

    @Test
    public void addMealTypeTest() throws Exception {
        MealType mealType = new MealType();
        mealType.name = MEAL_TYPE_NAME;

        when(service.addMealType(mealType)).thenReturn(dummyMealType);

        MvcResult mvcResult = post("/meal-types/", mealType)
                .andReturn();

        MealType actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), MealType.class);
        assertEquals(dummyMealType, actual);

        verify(service).addMealType(mealType);
    }

    @Test
    public void addMealTypeValidationErrorTest() throws Exception {
        MealType mealType = new MealType();

        post400("/meal-types/", mealType);

        verify(service, never()).addMealType(mealType);
    }

    @Test
    public void updateMealTypeTest() throws Exception {
        when(service.updateMealType(dummyMealType)).thenReturn(true);

        MvcResult mvcResult = put("/meal-types/" + MEAL_TYPE_ID, dummyMealType)
                .andReturn();

        MealType actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), MealType.class);
        assertEquals(dummyMealType, actual);

        verify(service).updateMealType(dummyMealType);
    }

    public void updateMealTypeValidationTest() throws Exception {
        String message = "Path variable Id = 55 doesn't match with request body Id = " + MEAL_TYPE_ID;

        put400("/meal-types/55", dummyMealType)
                .andExpect(jsonPath("$", is(message)));

        verify(service, never()).updateMealType(dummyMealType);

    }

    @Test
    public void updateMealTypeInternalErrorTest() throws Exception {
        String message = "Meal type failed to update";
        when(service.updateMealType(dummyMealType)).thenReturn(false);

        putJson("/meal-types/" + MEAL_TYPE_ID, dummyMealType)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).updateMealType(dummyMealType);
    }

    @Test
    public void deleteMealTypeTest() throws Exception {
        when(service.deleteMealType(MEAL_TYPE_ID)).thenReturn(true);

        delete("/meal-types/" + MEAL_TYPE_ID);

        verify(service).deleteMealType(MEAL_TYPE_ID);
    }

    @Test
    public void deleteMealTypeNotFoundTest() throws Exception {
        String message = "MealType doesn't exist";
        when(service.deleteMealType(MEAL_TYPE_ID)).thenThrow(new NotFoundException(message));

        delete404("/meal-types/" + MEAL_TYPE_ID)
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteMealType(MEAL_TYPE_ID);
    }

    @Test
    public void deleteMealTypeInternalErrorTest() throws Exception {
        String message = "Meal type failed to delete";
        when(service.deleteMealType(MEAL_TYPE_ID)).thenReturn(false);

        deleteJson("/meal-types/" + MEAL_TYPE_ID)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteMealType(MEAL_TYPE_ID);
    }
}
