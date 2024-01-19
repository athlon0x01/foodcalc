package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.service.meal.MealTypeDomainService;
import com.outdoor.foodcalc.model.meal.MealTypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private static final MealType TYPE_1 = new MealType(TYPE_1_ID, TYPE_1_NAME);
    private static final MealType TYPE_2 = new MealType(TYPE_2_ID, TYPE_2_NAME);

    private static final MealTypeView VIEW_1 = new MealTypeView(TYPE_1_ID, TYPE_1_NAME);
    private static final MealTypeView VIEW_2 = new MealTypeView(TYPE_2_ID, TYPE_2_NAME);

    @Mock
    private MealTypeDomainService domainService;

    @InjectMocks
    private MealTypesService mealTypesService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMealTypesTest() {
        List<MealType> domainMealTypes = Arrays.asList(TYPE_1, TYPE_2);
        when(domainService.getMealTypes()).thenReturn(domainMealTypes);
        List<MealTypeView> expected = Arrays.asList(VIEW_1, VIEW_2);

        List<MealTypeView> actual = mealTypesService.getMealTypes();

        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);

        verify(domainService).getMealTypes();
    }

    @Test
    public void getMealTypeTest() {
        MealType domainType = TYPE_1;
        when(domainService.getMealType(domainType.getTypeId())).thenReturn(Optional.of(domainType));

        MealTypeView actual = mealTypesService.getMealType(domainType.getTypeId());

        assertNotNull(actual);
        assertEquals(VIEW_1, actual);

        verify(domainService).getMealType(TYPE_1_ID);
    }

    @Test
    public void getMealTypeFailTest() {
        when(domainService.getMealType(TYPE_1_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> {
            mealTypesService.getMealType(TYPE_1_ID);
        });
    }

    @Test
    public void addMealTypeTest() {
        MealType typeToAdd = new MealType(-1, TYPE_1_NAME);
        when(domainService.addMealType(typeToAdd)).thenReturn(TYPE_1);

        MealTypeView actual = mealTypesService.addMealType(TYPE_1_NAME);

        assertNotNull(actual);
        assertEquals(VIEW_1, actual);

        verify(domainService).addMealType(typeToAdd);
    }

    @Test
    public void updateMealTypeTest() {
        doNothing().when(domainService).updateMealType(TYPE_1);

        mealTypesService.updateMealType(VIEW_1);

        verify(domainService).updateMealType(TYPE_1);
    }

    @Test
    public void updateCategoryNotFoundTest() {
        doThrow(NotFoundException.class).when(domainService).updateMealType(TYPE_1);

        Assertions.assertThrows(NotFoundException.class, () -> {
            mealTypesService.updateMealType(VIEW_1);
        });
    }

    @Test
    public void updateCategoryFailTest() {
        doThrow(FoodcalcDomainException.class).when(domainService).updateMealType(TYPE_1);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            mealTypesService.updateMealType(VIEW_1);
        });
    }

    @Test
    public void deleteMealTypeTest() {
        doNothing().when(domainService).deleteMealType(TYPE_1_ID);

        mealTypesService.deleteMealType(TYPE_1_ID);

        verify(domainService).deleteMealType(TYPE_1_ID);
    }

    @Test
    public void deleteDishCategoryNotFoundTest() {
        doThrow(NotFoundException.class).when(domainService).deleteMealType(TYPE_1_ID);

        Assertions.assertThrows(NotFoundException.class, () -> {
            mealTypesService.deleteMealType(TYPE_1_ID);
        });
    }

    @Test
    public void deleteDishCategoryFailTest() {
        doThrow(FoodcalcDomainException.class).when(domainService).deleteMealType(TYPE_1_ID);

        Assertions.assertThrows(FoodcalcDomainException.class, () -> {
            mealTypesService.deleteMealType(TYPE_1_ID);
        });
    }
}
