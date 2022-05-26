package com.outdoor.foodcalc.domain.service.meal;


import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.repository.meal.IMealTypeRepo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link MealTypeDomainService} class
 *
 * @author Olga Borovyk
 */
public class MealTypeDomainServiceTest {

    private static final int MEAL_TYPE_ID = 12345;
    private static final MealType dummyMealType = new MealType(MEAL_TYPE_ID, "dummyMealType");

    @InjectMocks
    private MealTypeDomainService mealTypeDomainService;

    @Mock
    private IMealTypeRepo mealTypeRepo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMealTypesTest() {
        List<MealType> expected = Collections.singletonList(dummyMealType);

        when(mealTypeRepo.getMealTypes()).thenReturn(expected);

        List<MealType> actual = mealTypeDomainService.getMealTypes();
        assertEquals(expected, actual);

        verify(mealTypeRepo).getMealTypes();
    }

    @Test
    public void getMealTypeTest() {
        Optional<MealType> expected = Optional.of(dummyMealType);

        when(mealTypeRepo.getMealType(MEAL_TYPE_ID)).thenReturn(expected);

        Optional<MealType> actual = mealTypeDomainService.getMealType(MEAL_TYPE_ID);
        assertEquals(expected, actual);

        verify(mealTypeRepo).getMealType(MEAL_TYPE_ID);
    }

    @Test
    public void addMealTypeTest() {
        MealType mealTypeToAdd = new MealType(-1, "dummyMealType");
        when(mealTypeRepo.addMealType(mealTypeToAdd)).thenReturn(MEAL_TYPE_ID);

        assertEquals(dummyMealType, mealTypeDomainService.addMealType(mealTypeToAdd));

        verify(mealTypeRepo).addMealType(mealTypeToAdd);
    }

    @Test
    public void updateMealTypeTest() {
        when(mealTypeRepo.exist(MEAL_TYPE_ID)).thenReturn(true);
        when(mealTypeRepo.updateMealType(dummyMealType)).thenReturn(false);

        assertFalse(mealTypeDomainService.updateMealType(dummyMealType));

        verify(mealTypeRepo).exist(MEAL_TYPE_ID);
        verify(mealTypeRepo).updateMealType(dummyMealType);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotExistingMealTypeTest() {
        when(mealTypeRepo.exist(MEAL_TYPE_ID)).thenReturn(false);

        mealTypeDomainService.updateMealType(dummyMealType);
    }

    @Test
    public void deleteMealTypeTest() {
        when(mealTypeRepo.exist(MEAL_TYPE_ID)).thenReturn(true);
        when(mealTypeRepo.deleteMealType(MEAL_TYPE_ID)).thenReturn(true);

        assertTrue(mealTypeDomainService.deleteMealType(MEAL_TYPE_ID));

        verify(mealTypeRepo).deleteMealType(MEAL_TYPE_ID);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotExistingMealTypeTest() {
        when(mealTypeRepo.exist(MEAL_TYPE_ID)).thenReturn(false);

        mealTypeDomainService.deleteMealType(MEAL_TYPE_ID);
    }
}
