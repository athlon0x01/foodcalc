package com.outdoor.foodcalc.domain.repository.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.pack.FoodPackage;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FoodPackageRepo extends AbstractRepository<FoodPackage> implements IFoodPackageRepo, RowMapper<FoodPackage> {

    static final String SELECT_PLAN_PACKAGES_SQL = "select fp.id as packageId, fp.name as name, fp.description as description, " +
            "fp.volume_coef as vCoef, fp.additional_weight as weight " +
            "from food_package fp " +
            "where plan = :planId";

    static final String INSERT_PACKAGE_SQL = "insert into food_package (name, description, volume_coef, additional_weight, plan) " +
            "values (:name, :description, :vCoef, :weight, :planId)";

    static final String UPDATE_PACKAGE_SQL = "update food_package set name = :name, description = :description, " +
            "volume_coef = :vCoef, additional_weight = :weight " +
            "where id = :packageId";

    static final String DELETE_PACKAGE_SQL = "delete from food_package where id = :packageId";

    static final String SELECT_PACKAGE_EXIST_SQL = "select count(*) from food_package where id = :packageId";

    @Override
    public List<FoodPackage> getPlanPackages(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("planId", planId);
        return jdbcTemplate.query(SELECT_PLAN_PACKAGES_SQL, parameters, this);
    }

    MapSqlParameterSource getSqlParameterSource(FoodPackage foodPackage) {
        return new MapSqlParameterSource()
                .addValue("name", foodPackage.getName())
                .addValue("description", foodPackage.getDescription())
                .addValue("vCoef", foodPackage.getVolumeCoefficient())
                .addValue("weight", foodPackage.getAdditionalWeightInt());
    }

    @Override
    public long addPackage(long planId, FoodPackage foodPackage) {
        MapSqlParameterSource parameters = getSqlParameterSource(foodPackage)
                .addValue("planId", planId);
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(INSERT_PACKAGE_SQL, parameters, holder, new String[]{"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    @Override
    public boolean updatePackage(FoodPackage foodPackage) {
        MapSqlParameterSource parameters = getSqlParameterSource(foodPackage)
                .addValue("packageId", foodPackage.getId());
        return jdbcTemplate.update(UPDATE_PACKAGE_SQL, parameters) > 0;
    }

    @Override
    public void deletePackage(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("packageId", id);
        jdbcTemplate.update(DELETE_PACKAGE_SQL, parameters);
    }

    @Override
    public boolean existsPackage(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("packageId", id);
        Long count = jdbcTemplate.queryForObject(SELECT_PACKAGE_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public FoodPackage mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return FoodPackage.builder()
                .id(resultSet.getLong("packageId"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .volumeCoefficient(resultSet.getFloat("vCoef"))
                .additionalWeight(resultSet.getInt("weight"))
                .build();
    }
}
