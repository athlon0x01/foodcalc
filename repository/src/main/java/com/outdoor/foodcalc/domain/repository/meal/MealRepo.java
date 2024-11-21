package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public class MealRepo extends AbstractRepository<Meal>
        implements IMealRepo, RowMapper<Meal>, ResultSetExtractor<Map<Long, List<Meal>>> {

    static final String SELECT_ALL_MEALS_FOR_PLAN_SQL = "select m.id as mealId, mt.id as typeId, mt.name as typeName, m.description as description, md.day as dayId, md.ndx as ndx " +
            "from day_meal md left join meal m on m.id = md.meal " +
            "left join meal_type mt on m.type = mt.id " +
            "where md.meal in (select day_meal.meal from day_meal where day_meal.day in (select id from day_plan where plan = :planId))";

    static final String SELECT_DAY_MEALS_SQL = "select m.id as mealId, mt.id as typeId, mt.name as typeName, m.description as description " +
            "from day_meal dm left join meal m on m.id = dm.meal left join meal_type mt on mt.id = m.type " +
            "where dm.day = :dayId " +
            "order by dm.ndx";

    static final String SELECT_MEAL_SQL = "select m.id as mealId, mt.id as typeId, mt.name as typeName, m.description as description " +
            "from meal m join meal_type mt on m.type = mt.id " +
            "where m.id = :mealId";

    static final String INSERT_MEAL_SQL = "insert into meal (type, description) values (:type, :description)";

    static final String INSERT_DAY_MEAL_LINK_SQL = "insert into day_meal (day, meal, ndx) " +
            "values (:dayId, :mealId, (select coalesce(max(ndx) + 1, 0) from day_meal where day = :dayId))";

    static final String UPDATE_MEAL_SQL = "update meal set type = :type, description = :description " +
            "where id = :mealId";

    static final String DELETE_MEAL_SQL = "delete from meal where id = :mealId";

    static final String DELETE_DAY_MEAL_LINK_SQL = "delete from day_meal where meal = :mealId";

    static final String SELECT_MEAL_EXIST_SQL = "select count(*) from meal where id = :id";

    private static final String MEAL_ID = "mealId";

    @Override
    public Map<Long, List<Meal>> getAllMealsInPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("planId", planId);
        return jdbcTemplate.query(SELECT_ALL_MEALS_FOR_PLAN_SQL, parameters, this::extractData);
    }

    @Override
    public List<Meal> getDayMeals(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("dayId", dayId);
        return jdbcTemplate.query(SELECT_DAY_MEALS_SQL, parameters, this::mapRow);
    }

    @Override
    public Optional<Meal> getMeal(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(MEAL_ID, mealId);
        return getSingleObject(SELECT_MEAL_SQL, parameters, this);
    }

    @Override
    public long addMeal(Meal meal) {
        KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource parameters = buildSqlParameterSource(meal);
        jdbcTemplate.update(INSERT_MEAL_SQL, parameters, holder, new String[] {"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    private MapSqlParameterSource buildSqlParameterSource(Meal meal) {
        return new MapSqlParameterSource()
                .addValue("type", meal.getType().getTypeId())
                .addValue("description", meal.getDescription());
    }

    @Override
    public void attachMeal(long dayId, long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(MEAL_ID, mealId)
                .addValue("dayId", dayId);
        jdbcTemplate.update(INSERT_DAY_MEAL_LINK_SQL, parameters);
    }

    @Override
    public boolean updateMeal(Meal meal) {
        MapSqlParameterSource parameters = buildSqlParameterSource(meal)
                .addValue(MEAL_ID, meal.getMealId());
        return jdbcTemplate.update(UPDATE_MEAL_SQL, parameters) > 0;
    }

    @Override
    public boolean deleteMeal(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(MEAL_ID, id);
        return jdbcTemplate.update(DELETE_MEAL_SQL, parameters) > 0;
    }

    @Override
    public void detachMeal(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(MEAL_ID, mealId);
        jdbcTemplate.update(DELETE_DAY_MEAL_LINK_SQL, parameters);
    }

    @Override
    public boolean existsMeal(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("id", id);
        Long count = jdbcTemplate.queryForObject(SELECT_MEAL_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public Meal mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        MealType type = new MealType(resultSet.getLong("typeId"), resultSet.getString("typeName"));
        return Meal.builder()
                .mealId(resultSet.getLong(MEAL_ID))
                .type(type)
                .description(resultSet.getString("description"))
                .build();
    }

    @Override
    public Map<Long, List<Meal>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, TreeMap<Integer, Meal>> allMeals = new HashMap<>();
        while (rs.next()) {
            Long itemId = rs.getLong("dayId");
            //internal map to store meals in proper order
            TreeMap<Integer, Meal> dayMeals = allMeals.computeIfAbsent(itemId, k -> new TreeMap<>());
            Meal meal = mapRow(rs, rs.getRow());
            //we don't store index in the entities, so we use Map with index as a key to sort dishes properly
            int ndx = rs.getInt("ndx");
            dayMeals.put(ndx, meal);
        }
        return allMeals.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue().values())));
    }
}
