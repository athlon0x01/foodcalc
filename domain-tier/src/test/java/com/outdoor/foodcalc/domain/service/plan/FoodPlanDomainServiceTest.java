package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.service.FoodPlansRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodPlanDomainServiceTest {

    @Mock
    private IFoodPlanRepo planRepo;
    @Mock
    private FoodPlansRepo tmpRepo;

    @InjectMocks
    private FoodPlanDomainService service;

    @Test
    void getFoodPlan() {
        FoodPlan plan = FoodPlan.builder().id(123L).description("Dummy Plan").members(3).build();
        PlanDay day = PlanDay.builder().dayId(54321L).date(LocalDate.of(2024, 8, 21)).build();

        when(planRepo.getFoodPlan(123L)).thenReturn(Optional.of(plan));
        when(tmpRepo.getPlanDays(123L)).thenReturn(List.of(day));

        Optional<FoodPlan> planOptional = service.getFoodPlan(123L);
        assertTrue(planOptional.isPresent());
        FoodPlan actualPlan = planOptional.get();
        assertFalse(actualPlan.getDays().isEmpty());
    }
}