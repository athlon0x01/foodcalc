package com.outdoor.foodcalc.model.plan.pack;

import com.outdoor.foodcalc.model.plan.HikerInfo;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
public class PlanWithPackagesView {
    private List<FoodPackageView> packages;
    private List<HikerInfo> members;
}
