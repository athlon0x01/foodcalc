package com.outdoor.foodcalc.domain.service.plan;

import com.outdoor.foodcalc.domain.exception.FoodcalcDomainException;
import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.repository.plan.IFoodPlanRepo;
import com.outdoor.foodcalc.domain.repository.plan.IHikerRepo;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HikerDomainService {

    private final IFoodPlanRepo planRepo;
    private final IHikerRepo hikerRepo;

    public HikerDomainService(IFoodPlanRepo planRepo, IHikerRepo hikerRepo) {
        this.planRepo = planRepo;
        this.hikerRepo = hikerRepo;
    }

    public List<Hiker> getPlanHikers(long planId) {
        return hikerRepo.getPlanHikers(planId);
    }

    public Optional<Hiker> geHiker(long id) {
        return hikerRepo.getHiker(id);
    }

    public Hiker addHiker(long planId, Hiker hiker) {
        long id = hikerRepo.addHiker(planId, hiker);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
        return hiker.toBuilder()
                .id(id)
                .build();
    }

    public void updateHiker(long planId, Hiker hiker) {
        if (!hikerRepo.existsHiker(hiker.getId())) {
            throw new NotFoundException("Hiker with id=" + hiker.getId() + " doesn't exist");
        }
        if(!hikerRepo.updateHiker(hiker)) {
            throw new FoodcalcDomainException("Failed to update hiker with id=" + hiker.getId());
        }
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }

    public void deleteHiker(long planId, long id) {
        if (!hikerRepo.existsHiker(id)) {
            throw new NotFoundException("Hiker with id=" + id + " doesn't exist");
        }
        hikerRepo.deleteHiker(id);
        planRepo.saveLastUpdated(planId, ZonedDateTime.now());
    }
}
