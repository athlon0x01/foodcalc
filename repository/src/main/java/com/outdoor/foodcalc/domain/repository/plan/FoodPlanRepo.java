package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.FoodPlan;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FoodPlanRepo extends AbstractRepository<FoodPlan> implements IFoodPlanRepo, RowMapper<FoodPlan> {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String MEMBERS = "members";
    private static final String CREATED_ON = "createdOn";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String DESCRIPTION = "description";

    static final String SELECT_ALL_FOOD_PLANS_SQL = "select fp.id as id, fp.name as name, fp.members as members, " +
            "fp.createdon as createdOn, fp.lastupdated as lastUpdated, fp.description as description, " +
            "(select count(*) from day_plan where plan = fp.id) as duration " +
            "from food_plan fp order by fp.lastupdated desc";

    static final String SELECT_FOOD_PLAN_SQL = "select fp.id as id, fp.name as name, fp.members as members, " +
            "fp.createdon as createdOn, fp.lastupdated as lastUpdated, fp.description as description, 0 as duration " +
            "from food_plan fp " +
            "where id = :id";

    static final String INSERT_FOOD_PLAN_SQL = "insert into food_plan (name, members, createdon, lastupdated, description) " +
            "values (:name, :members, :createdOn, :lastUpdated, :description)";

    static final String UPDATE_FOOD_PLAN_SQL = "update food_plan set name = :name, members = :members, " +
            "lastupdated = :lastUpdated, description = :description where id = :id";

    static final String DELETE_FOOD_PLAN_SQL = "delete from food_plan where id = :id";

    static final String SELECT_FOOD_PLAN_EXIST_SQL = "select count(*) from food_plan where id = :id";

    static final String SELECT_DAY_COUNTS_FOR_PLAN_SQL = "select count(*) from day_plan where plan = :planId";

    static final String UPDATE_FOOD_PLAN_LAST_UPDATED_SQL = "update food_plan set lastupdated = :lastUpdated " +
            "where id = :id";

    @Override
    public List<FoodPlan> getAllFoodPlans() {
        return jdbcTemplate.query(SELECT_ALL_FOOD_PLANS_SQL, this);
    }

    @Override
    public Optional<FoodPlan> getFoodPlan(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);
        return getSingleObject(SELECT_FOOD_PLAN_SQL, parameters, this);
    }

    @Override
    public long addFoodPlan(FoodPlan plan) {
        KeyHolder holder = getKeyHolder();
        MapSqlParameterSource parameters = getSqlParameterSource(plan)
                .addValue(CREATED_ON, plan.getCreatedOn().toOffsetDateTime());
        jdbcTemplate.update(INSERT_FOOD_PLAN_SQL, parameters, holder, new String[] {ID});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    MapSqlParameterSource getSqlParameterSource(FoodPlan plan) {
        return new MapSqlParameterSource()
                .addValue(ID, plan.getId())
                .addValue(NAME, plan.getName())
                .addValue(MEMBERS, plan.getMembers())
                .addValue(DESCRIPTION, plan.getDescription())
                .addValue(LAST_UPDATED, plan.getLastUpdated().toOffsetDateTime());
    }

    @Override
    public boolean updateFoodPlan(FoodPlan plan) {
        MapSqlParameterSource parameters = getSqlParameterSource(plan);
        return jdbcTemplate.update(UPDATE_FOOD_PLAN_SQL, parameters) > 0;
    }

    @Override
    public boolean deleteFoodPlan(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);
        return jdbcTemplate.update(DELETE_FOOD_PLAN_SQL, parameters) > 0;
    }

    @Override
    public boolean existsFoodPlan(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);
        Long count = jdbcTemplate.queryForObject(SELECT_FOOD_PLAN_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public void saveLastUpdated(long planId, ZonedDateTime lastUpdated) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, planId)
                .addValue(LAST_UPDATED, lastUpdated.toOffsetDateTime());
        jdbcTemplate.update(UPDATE_FOOD_PLAN_LAST_UPDATED_SQL, parameters);
    }

    @Override
    public int getDaysCountForPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("planId", planId);
        Integer count = jdbcTemplate.queryForObject(SELECT_DAY_COUNTS_FOR_PLAN_SQL, parameters, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public FoodPlan mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        OffsetDateTime odtCreatedOn = resultSet.getObject(CREATED_ON, OffsetDateTime.class);
        ZonedDateTime createdOn = odtCreatedOn.toZonedDateTime();

        OffsetDateTime odtLastUpdated = resultSet.getObject(LAST_UPDATED, OffsetDateTime.class);
        ZonedDateTime lastUpdated = odtLastUpdated.toZonedDateTime();

        //TODO introduce new class PlanInfo to pass plan duration to home page
        List<PlanDay> days = new ArrayList<>();
        int duration = resultSet.getInt("duration");
        for (int i = 0; i < duration; i++) {
            days.add(PlanDay.builder().build());
        }
        return FoodPlan.builder()
                .id(resultSet.getLong(ID))
                .name(resultSet.getString(NAME))
                .members(resultSet.getInt(MEMBERS))
                .description(resultSet.getString(DESCRIPTION))
                .createdOn(createdOn)
                .lastUpdated(lastUpdated)
                .days(days)
                .build();
    }

    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
