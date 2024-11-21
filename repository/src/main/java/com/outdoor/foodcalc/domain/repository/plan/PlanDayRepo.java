package com.outdoor.foodcalc.domain.repository.plan;

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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class PlanDayRepo extends AbstractRepository<PlanDay> implements IPlanDayRepo, RowMapper<PlanDay> {

    static final String SELECT_PLAN_DAYS_SQL = "select dp.id as dayId, dp.day as day, dp.description as description " +
            "from day_plan dp " +
            "where dp.plan = :planId " +
            "order by dp.ndx";

    static final String SELECT_PLAN_DAY_SQL = "select dp.id as dayId, dp.day as day, dp.description as description " +
            "from day_plan dp " +
            "where dp.plan = :planId and dp.id = :dayId";

    static final String INSERT_FOOD_PLAN_DAY_SQL = "insert into day_plan (day, ndx, description, plan) " +
            "values (:day, (select count(*) from day_plan where plan = :planId), :description, :planId)";

    static final String UPDATE_FOOD_PLAN_DAY_SQL = "update day_plan set day = :day, description = :description " +
            "where id = :dayId";

    static final String UPDATE_FOOD_PLAN_DAY_INDEX_SQL = "update day_plan set ndx = :ndx " +
            "where id = :dayId";

    static final String DELETE_FOOD_PLAN_DAY_SQL = "delete from day_plan where id = :dayId";

    static final String SELECT_FOOD_PLAN_DAY_EXIST_SQL = "select count(*) from day_plan where id = :dayId";

    static final String SELECT_MEAL_COUNTS_FOR_DAY_SQL = "select count(*) from day_meal where day = :dayId";

    @Override
    public List<PlanDay> getPlanDays(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("planId", planId);
        return jdbcTemplate.query(SELECT_PLAN_DAYS_SQL, parameters, this);
    }

    @Override
    public Optional<PlanDay> getPlanDay(long planId, long id) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("planId", planId)
                .addValue("dayId", id);
        return getSingleObject(SELECT_PLAN_DAY_SQL, parameters, this);
    }

    MapSqlParameterSource getSqlParameterSource(PlanDay planDay) {
        return new MapSqlParameterSource()
                .addValue("day", planDay.getDate())
                .addValue("description", planDay.getDescription());
    }

    @Override
    public long addPlanDay(long planId, PlanDay day) {
        KeyHolder holder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = getSqlParameterSource(day)
                .addValue("ndx", 0)
                .addValue("planId", planId);
        jdbcTemplate.update(INSERT_FOOD_PLAN_DAY_SQL, parameters, holder, new String[] {"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    @Override
    public boolean updatePlanDay(PlanDay day) {
        MapSqlParameterSource parameters = getSqlParameterSource(day)
                .addValue("dayId", day.getDayId());
        return jdbcTemplate.update(UPDATE_FOOD_PLAN_DAY_SQL, parameters) > 0;
    }

    @Override
    public void updatePlanDayIndex(long dayId, int index) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("dayId", dayId)
                .addValue("ndx", index);
        jdbcTemplate.update(UPDATE_FOOD_PLAN_DAY_INDEX_SQL, parameters);
    }

    @Override
    public boolean deletePlanDay(long planId, long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", id);
        return jdbcTemplate.update(DELETE_FOOD_PLAN_DAY_SQL, parameters) > 0;
    }

    @Override
    public boolean existsPlanDay(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", id);
        Long count = jdbcTemplate.queryForObject(SELECT_FOOD_PLAN_DAY_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public int getMealsCountForDay(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        Integer count = jdbcTemplate.queryForObject(SELECT_MEAL_COUNTS_FOR_DAY_SQL, parameters, Integer.class);
        return count != null ? count : 0;
    }

    @Override
    public PlanDay mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return PlanDay.builder()
                .dayId(resultSet.getLong("dayId"))
                .date(resultSet.getObject("day", LocalDate.class))
                .description(resultSet.getString("description"))
                .build();
    }
}
