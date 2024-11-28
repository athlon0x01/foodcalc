package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.exception.NotFoundException;
import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.service.plan.HikerDomainService;
import com.outdoor.foodcalc.model.plan.HikerInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HikerService {

    private final HikerDomainService hikerDomainService;

    public HikerService(HikerDomainService hikerDomainService) {
        this.hikerDomainService = hikerDomainService;
    }

    public HikerInfo getHiker(long id) {
        return hikerDomainService.geHiker(id)
                .map(this::mapInfo)
                .orElseThrow(() -> new NotFoundException("Hiker with id = " + id + " wasn't found"));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void deleteHiker(long planId, long id) {
        hikerDomainService.deleteHiker(planId, id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public HikerInfo addHiker(long planId, HikerInfo hikerInfo) {
        Hiker hiker = Hiker.builder()
                .name(hikerInfo.getName())
                .weightCoefficient(hikerInfo.getWeightCoefficient())
                .build();
        return mapInfo(hikerDomainService.addHiker(planId, hiker));
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateHiker(long planId, HikerInfo hikerInfo) {
        Hiker hiker = Hiker.builder()
                .id(hikerInfo.getId())
                .name(hikerInfo.getName())
                .description(hikerInfo.getDescription())
                .weightCoefficient(hikerInfo.getWeightCoefficient())
                .build();
        hikerDomainService.updateHiker(planId, hiker);
    }

    public HikerInfo mapInfo(Hiker hiker) {
        return HikerInfo.builder()
                .id(hiker.getId())
                .name(hiker.getName())
                .description(hiker.getDescription())
                .weightCoefficient(hiker.getWeightCoefficient())
                .build();
    }
}
