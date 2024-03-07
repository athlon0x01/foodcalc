package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class FoodPlanRepo extends AbstractRepository<FoodPlan> implements IFoodPlanRepo, RowMapper<FoodPlan> {

    static final String SELECT_ALL_FOOD_PLANS_SQL = "select fp.id as id, fp.name as name, fp.members as members, " +
            "fp.createdon as createdOn, fp.lastupdated as lastUpdated, fp.description as description " +
            "from food_plan fp";

    static final String SELECT_FOOD_PLAN_SQL = "select fp.id as id, fp.name as name, fp.members as members, " +
            "fp.createdon as createdOn, fp.lastupdated as lastUpdated, fp.description as description " +
            "from food_plan fp " +
            "where id = :id";

    static final String INSERT_FOOD_PLAN_SQL = "insert into food_plan (name, members, createdon, lastupdated, description) " +
            "values (:name, :members, :createdOn, :lastUpdated, :description)";

    static final String UPDATE_FOOD_PLAN_SQL = "update food_plan set name = :name, members = :members, " +
            "lastupdated = :lastUpdated, description = :description where id = :id";

    static final String DELETE_FOOD_PLAN_SQL = "delete from food_plan  where id = :id";

    static final String SELECT_FOOD_PLAN_EXIST_SQL = "select count(*) from food_plan where id = :id";

    static final String UPDATE_FOOD_PLAN_LAST_UPDATED_SQL = "update food_plan set lastupdated = :lastUpdated " +
            "where id = :id";

    @Override
    public List<FoodPlan> getAllFoodPlans() {
        return jdbcTemplate.query(SELECT_ALL_FOOD_PLANS_SQL, this);
    }

    @Override
    public Optional<FoodPlan> getFoodPlan(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
        return getSingleObject(SELECT_FOOD_PLAN_SQL, parameters, this);
    }

    @Override
    public long addFoodPlan(FoodPlan plan) {
        KeyHolder holder = getKeyHolder();
        MapSqlParameterSource parameters = getSqlParameterSource(plan);
        jdbcTemplate.update(INSERT_FOOD_PLAN_SQL, parameters, holder, new String[] {"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    MapSqlParameterSource getSqlParameterSource(FoodPlan plan) {
        return new MapSqlParameterSource()
                .addValue("id", plan.getId())
                .addValue("name", plan.getName())
                .addValue("members", plan.getMembers())
                .addValue("createdOn", plan.getCreatedOn().toOffsetDateTime())
                .addValue("lastUpdated", plan.getLastUpdated().toOffsetDateTime())
                .addValue("description", plan.getDescription());
    }

    @Override
    public boolean updateFoodPlan(FoodPlan plan) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", plan.getId())
                .addValue("name", plan.getName())
                .addValue("members", plan.getMembers())
                .addValue("lastUpdated", plan.getLastUpdated().toOffsetDateTime())
                .addValue("description", plan.getDescription());
        return jdbcTemplate.update(UPDATE_FOOD_PLAN_SQL, parameters) > 0;
    }

    @Override
    public boolean deleteFoodPlan(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
        return jdbcTemplate.update(DELETE_FOOD_PLAN_SQL, parameters) > 0;
    }

    @Override
    public boolean existsFoodPlan(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
        Long count = jdbcTemplate.queryForObject(SELECT_FOOD_PLAN_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public boolean saveLastUpdated(long planId, ZonedDateTime lastUpdated) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", planId)
                .addValue("lastUpdated", lastUpdated.toOffsetDateTime());
        return jdbcTemplate.update(UPDATE_FOOD_PLAN_LAST_UPDATED_SQL, parameters) > 0;
    }

    @Override
    public FoodPlan mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        OffsetDateTime odtCreatedOn = resultSet.getObject("createdOn", OffsetDateTime.class);
        ZonedDateTime createdOn = odtCreatedOn.toZonedDateTime();

        OffsetDateTime odtLastUpdated = resultSet.getObject("lastUpdated", OffsetDateTime.class);
        ZonedDateTime lastUpdated = odtLastUpdated.toZonedDateTime();

        return FoodPlan.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .members(resultSet.getInt("members"))
                .createdOn(createdOn)
                .lastUpdated(lastUpdated)
                .description(resultSet.getString("description"))
                .build();
    }

    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
