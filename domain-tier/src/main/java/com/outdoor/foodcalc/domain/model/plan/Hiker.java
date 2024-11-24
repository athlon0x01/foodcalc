package com.outdoor.foodcalc.domain.model.plan;

import com.outdoor.foodcalc.domain.model.IDomainEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

import java.util.Objects;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Jacksonized
@Builder(toBuilder = true)
public class Hiker implements IDomainEntity {

    @EqualsAndHashCode.Include
    private final long id;
    private String name;
    private String description;
    private float weightCoefficient;

    @Override
    public boolean sameValueAs(IDomainEntity other) {
        if (this.equals(other)) {
            Hiker that = (Hiker) other;

            if (!Objects.equals(name, that.name)) return false;
            if (!Objects.equals(description, that.description)) return false;
            return Objects.equals(weightCoefficient, that.weightCoefficient);
        }
        return false;
    }
}
