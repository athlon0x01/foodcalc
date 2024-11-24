package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.Hiker;

import java.util.List;
import java.util.Optional;

public interface IHikerRepo {

    List<Hiker> getPlanHikers(long planId);

    Optional<Hiker> getHiker(long id);

    long addHiker(long planId, Hiker hiker);

    boolean updateHiker(Hiker hiker);

    void deleteHiker(long id);

    boolean existsHiker(long id);
}
