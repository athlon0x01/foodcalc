package com.outdoor.foodcalc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import com.outdoor.foodcalc.service.MealTypesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
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

@SpringBootTest
public class MealTypesEndpointTest extends ApiUnitTest{

    private static final int MEAL_TYPE_ID = 12345;
    private static final String MEAL_TYPE_NAME = "Test meal type";

    private static final MealTypeView dummyMealTypeView = new MealTypeView(MEAL_TYPE_ID, MEAL_TYPE_NAME);

    @MockBean
    private MealTypesService service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Override
    public void setMapper(ObjectMapper mapper) {
        super.setMapper(mapper);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setMockMvc(MockMvcBuilders.webAppContextSetup(webApplicationContext).build());
    }

    @Test
    public void getMealTypesTest() throws Exception {
        List<MealTypeView> expected = Collections.singletonList(dummyMealTypeView);

        when(service.getMealTypes()).thenReturn(expected);

        MvcResult mvcResult = get("/meal-types")
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();

        MealTypeView[] actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), MealTypeView[].class);
        assertEquals(expected, Arrays.asList(actual));

        verify(service).getMealTypes();
    }

    @Test
    public void getMealTypeTest() throws Exception {
        MealTypeView expected = dummyMealTypeView;

        when(service.getMealType(dummyMealTypeView.getId())).thenReturn(expected);

        MvcResult mvcResult = get("/meal-types/" + MEAL_TYPE_ID).andReturn();

        MealTypeView actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), MealTypeView.class);
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
        MealTypeView mealTypeView = new MealTypeView();
        mealTypeView.setName(MEAL_TYPE_NAME);

        when(service.addMealType(mealTypeView.getName())).thenReturn(dummyMealTypeView);

        MvcResult mvcResult = post("/meal-types/", mealTypeView)
                .andReturn();

        MealTypeView actual = mapper.readValue(mvcResult.getResponse().getContentAsString(), MealTypeView.class);
        assertEquals(dummyMealTypeView, actual);

        verify(service).addMealType(mealTypeView.getName());
    }

    @Test
    public void addMealTypeValidationErrorTest() throws Exception {
        MealTypeView mealTypeView = new MealTypeView();

        post400("/meal-types/", mealTypeView);

        verify(service, never()).addMealType(mealTypeView.getName());
    }

    @Test
    public void updateMealTypeTest() throws Exception {
        doNothing().when(service).updateMealType(dummyMealTypeView);

        put("/meal-types/" + MEAL_TYPE_ID, dummyMealTypeView)
                .andReturn();

        verify(service).updateMealType(dummyMealTypeView);
    }

    @Test
    public void updateMealTypeValidationTest() throws Exception {
        String message = "Path variable Id = 55 doesn't match with request body Id = " + MEAL_TYPE_ID;

        put400("/meal-types/55", dummyMealTypeView)
                .andExpect(jsonPath("$", is(message)));

        verify(service, never()).updateMealType(dummyMealTypeView);

    }

    @Test
    public void updateMealTypeNotFoundTest() throws Exception {
        String message = "Meal type with id=" + MEAL_TYPE_ID + " doesn't exist";
        doThrow(new NotFoundException(message)).when(service).updateMealType(dummyMealTypeView);

        put404("/meal-types/" + MEAL_TYPE_ID, dummyMealTypeView)
                .andExpect(jsonPath("$", is(message)));

        verify(service).updateMealType(dummyMealTypeView);
    }

    @Test
    public void updateMealTypeInternalErrorTest() throws Exception {
        String message = "Failed to delete meal type with id=" + MEAL_TYPE_ID;
        doThrow(new FoodcalcDomainException(message)).when(service).updateMealType(dummyMealTypeView);


        putJson("/meal-types/" + MEAL_TYPE_ID, dummyMealTypeView)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).updateMealType(dummyMealTypeView);
    }

    @Test
    public void deleteMealTypeTest() throws Exception {
        doNothing().when(service).deleteMealType(MEAL_TYPE_ID);

        delete("/meal-types/" + MEAL_TYPE_ID);

        verify(service).deleteMealType(MEAL_TYPE_ID);
    }

    @Test
    public void deleteMealTypeNotFoundTest() throws Exception {
        String message = "Meal type with id=" + MEAL_TYPE_ID + " doesn't exist";
        doThrow(new NotFoundException(message)).when(service).deleteMealType(MEAL_TYPE_ID);

        delete404("/meal-types/" + MEAL_TYPE_ID)
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteMealType(MEAL_TYPE_ID);
    }

    @Test
    public void deleteMealTypeInternalErrorTest() throws Exception {
        String message = "Failed to delete meal type with id=" + MEAL_TYPE_ID;
        doThrow(new FoodcalcDomainException(message)).when(service).deleteMealType(MEAL_TYPE_ID);

        deleteJson("/meal-types/" + MEAL_TYPE_ID)
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", is(message)));

        verify(service).deleteMealType(MEAL_TYPE_ID);
    }
}
