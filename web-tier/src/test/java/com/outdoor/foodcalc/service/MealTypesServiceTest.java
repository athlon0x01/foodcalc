package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Service for all operations with {@link MealType} objects,
 * including transformation domain objects to view models and vise versa.
 *
 * @author Olga Borovyk.
 */
public class MealTypesServiceTest {
    private static final int TYPE_1_ID = 11111;
    private static final int TYPE_2_ID = 22222;
    private static final String TYPE_1_NAME = "First Meal type";
    private static final String TYPE_2_NAME = "Second Meal type";

    @Mock
    private MealTypeDomainService domainService;

    @InjectMocks
    private MealTypesService mealTypesService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMealTypesTest() {
        List<MealType> domainMealTypes = Arrays.asList(
                new MealType(TYPE_1_ID, TYPE_1_NAME),
                new MealType(TYPE_2_ID, TYPE_2_NAME));
        when(domainService.getMealTypes()).thenReturn(domainMealTypes);

        List<MealTypeView> actual = mealTypesService.getMealTypes();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(TYPE_1_ID, actual.get(0).id);
        assertEquals(TYPE_1_NAME, actual.get(0).name);
        assertEquals(TYPE_2_ID, actual.get(1).id);
        assertEquals(TYPE_2_NAME, actual.get(1).name);

        verify(domainService).getMealTypes();
    }

    @Test
    public void getMealTypeTest() {
        MealType domainType = new MealType(TYPE_1_ID, TYPE_1_NAME);
        when(domainService.getMealType(TYPE_1_ID)).thenReturn(Optional.of(domainType));

        MealTypeView actual = mealTypesService.getMealType(TYPE_1_ID);

        assertNotNull(actual);
        assertEquals(TYPE_1_ID, actual.id);
        assertEquals(TYPE_1_NAME, actual.name);

        verify(domainService).getMealType(TYPE_1_ID);
    }

    @Test(expected = NotFoundException.class)
    public void getMealTypeFailTest() {
        when(domainService.getMealType(TYPE_1_ID)).thenReturn(Optional.empty());

        mealTypesService.getMealType(TYPE_1_ID);
    }

    @Test
    public void addMealTypeTest() {
        MealType returnedDomainType = new MealType(TYPE_1_ID, TYPE_1_NAME);
        MealType typeToAdd = new MealType(-1, TYPE_1_NAME);
        when(domainService.addMealType(typeToAdd)).thenReturn(returnedDomainType);

        MealTypeView actual = mealTypesService.addMealType(TYPE_1_NAME);

        assertNotNull(actual);
        assertEquals(TYPE_1_ID, actual.id);
        assertEquals(TYPE_1_NAME, actual.name);

        verify(domainService).addMealType(typeToAdd);
    }

    @Test
    public void updateMealTypeTest() {
        MealTypeView typeToUpdate = new MealTypeView();
        typeToUpdate.id = TYPE_1_ID;
        typeToUpdate.name = TYPE_1_NAME;
        MealType domainType = new MealType(TYPE_1_ID, TYPE_1_NAME);
        when(domainService.updateMealType(domainType)).thenReturn(true);

        assertTrue(mealTypesService.updateMealType(typeToUpdate));

        verify(domainService).updateMealType(domainType);
    }

    @Test
    public void deleteMealTypeTest() {
        when(domainService.deleteMealType(TYPE_1_ID)).thenReturn(true);

        assertTrue(mealTypesService.deleteMealType(TYPE_1_ID));

        verify(domainService).deleteMealType(TYPE_1_ID);
    }
}
