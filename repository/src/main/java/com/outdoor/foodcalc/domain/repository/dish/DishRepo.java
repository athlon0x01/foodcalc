package com.outdoor.foodcalc.domain.repository.dish;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
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
public class DishRepo extends AbstractRepository<Dish>
        implements IDishRepo, RowMapper<Dish>, ResultSetExtractor<Map<Long, List<Dish>>> {

    static final String SELECT_ALL_TEMPLATE_DISHES_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName, d.template as template from dish d join dish_category c on d.category = c.id " +
            "where d.template = true";

    static final String SELECT_MEAL_DISHES_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName, d.template as template " +
            "from meal_dish md left join dish d on d.id = md.dish " +
            "left join dish_category c on d.category = c.id " +
            "where md.meal = :mealId order by md.ndx";

    static final String SELECT_DAY_DISHES_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName, d.template as template " +
            "from day_dish dd left join dish d on d.id = dd.dish " +
            "left join dish_category c on d.category = c.id " +
            "where dd.day = :dayId order by dd.ndx";

    static final String SELECT_MEAL_DISHES_FOR_ALL_MEALS_IN_DAY_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName, d.template as template, md.meal as itemId, md.ndx as ndx " +
            "from meal_dish md left join dish d on d.id = md.dish " +
            "left join dish_category c on d.category = c.id " +
            "where md.meal in (select day_meal.meal from day_meal where day = :dayId)";

    static final String SELECT_MEAL_DISHES_FOR_ALL_MEALS_IN_PLAN_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName, d.template as template, md.meal as itemId, md.ndx as ndx " +
            "from meal_dish md left join dish d on d.id = md.dish " +
            "left join dish_category c on d.category = c.id " +
            "where md.meal in (select day_meal.meal from day_meal where day_meal.day in (select id from day_plan where plan = :planId))";

    static final String SELECT_DAY_DISHES_FOR_ALL_DAYS_IN_PLAN_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName, d.template as template, dd.day as itemId, dd.ndx as ndx " +
            "from day_dish dd left join dish d on d.id = dd.dish " +
            "left join dish_category c on d.category = c.id " +
            "where dd.day in (select id from day_plan where plan = :planId)";

    static final String SELECT_DISH_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName, d.template as template from dish d join dish_category c on d.category = c.id where d.id = :dishId";
    static final String INSERT_DISH_SQL = "insert into dish (name, description, category, template) " +
            "values (:name, :description, :categoryId, :template)";
    static final String UPDATE_DISH_SQL = "update dish set name = :name, description = :description, " +
            "category = :categoryId where id = :dishId";
    static final String DELETE_DISH_SQL = "delete from dish where id = :dishId";
    static final String SELECT_DISHES_COUNT_IN_CATEGORY_SQL = "select count(*)  from dish d join dish_category c " +
            "on d.category = c.id and c.id = :categoryId";
    static final String SELECT_DISH_EXIST_SQL = "select count(*) from dish where id = :dishId";

    static final String INSERT_DAY_DISH_LINK_SQL = "insert into day_dish (day, dish, ndx) " +
            "values (:dayId, :dishId, (select coalesce(max(ndx) + 1, 0) from day_dish where day = :dayId))";

    static final String DELETE_DAY_DISH_LINK_SQL = "delete from day_dish where day = :dayId and dish = :dishId";

    static final String INSERT_MEAL_DISH_LINK_SQL = "insert into meal_dish (meal, dish, ndx) " +
            "values (:mealId, :dishId, (select coalesce(max(ndx) + 1, 0) from meal_dish where meal = :mealId))";

    static final String DELETE_MEAL_DISH_LINK_SQL = "delete from meal_dish where meal = :mealId and dish = :dishId";

    static final String DELETE_ALL_DISHES_LINKS_FOR_MEAL_SQL = "delete from meal_dish where meal = :mealId";

    static final String DELETE_ALL_DISHES_LINKS_FOR_DAY_SQL = "delete from day_dish where day = :dayId";

    static final String DELETE_ALL_DISHES_FOR_MEAL_SQL = "delete from dish " +
            "where id in (select md.dish from meal_dish md where md.meal = :mealId)";

    static final String DELETE_ALL_DISHES_FOR_DAY_SQL = "delete from dish " +
            "where id in (select dd.dish from day_dish dd where dd.day = :dayId)";

    @Override
    public List<Dish> getAllTemplateDishes() {
        return jdbcTemplate.query(SELECT_ALL_TEMPLATE_DISHES_SQL, this::mapRow);
    }

    @Override
    public long countDishesInCategory(long categoryId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("categoryId", categoryId);
        Long dishesCount = jdbcTemplate.queryForObject(SELECT_DISHES_COUNT_IN_CATEGORY_SQL, parameters, Long.class);
        return dishesCount == null ? 0 : dishesCount;
    }

    @Override
    public Optional<Dish> getDish(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dishId", id);
        return getSingleObject(SELECT_DISH_SQL, parameters, this);
    }

    @Override
    public long addDish(Dish dish) {
        KeyHolder holder = getKeyHolder();
        MapSqlParameterSource parameters = getSqlParameterSource(dish);
        jdbcTemplate.update(INSERT_DISH_SQL, parameters, holder, new String[] {"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    MapSqlParameterSource getSqlParameterSource(Dish dish) {
        return new MapSqlParameterSource()
                .addValue("dishId", dish.getDishId())
                .addValue("name", dish.getName())
                .addValue("description", dish.getDescription())
                .addValue("categoryId", dish.getCategory().getCategoryId())
                .addValue("template", dish.isTemplate());
    }

    @Override
    public boolean updateDish(Dish dish) {
        MapSqlParameterSource parameters = getSqlParameterSource(dish);
        return jdbcTemplate.update(UPDATE_DISH_SQL, parameters) > 0;
    }

    @Override
    public boolean deleteDish(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dishId", id);
        return jdbcTemplate.update(DELETE_DISH_SQL, parameters) > 0;
    }

    @Override
    public boolean existsDish(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dishId", id);
        Long count = jdbcTemplate.queryForObject(SELECT_DISH_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public Dish mapRow(ResultSet resultSet, int i) throws SQLException {
        final DishCategory category = new DishCategory(
                resultSet.getLong("catId"), resultSet.getString("catName"));
        return Dish.builder()
                .dishId(resultSet.getLong("dishId"))
                .name(resultSet.getString("dishName"))
                .description(resultSet.getString("description"))
                .category(category)
                .template(resultSet.getBoolean("template"))
                .build();
    }

    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }

    @Override
    public Map<Long, List<Dish>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, TreeMap<Integer, Dish>> allItemsDishes = new HashMap<>();
        while (rs.next()) {
            Long itemId = rs.getLong("itemId");
            //internal map to store dishes for meal or day
            TreeMap<Integer, Dish> itemDishes = allItemsDishes.computeIfAbsent(itemId, k -> new TreeMap<>());
            Dish dish = mapRow(rs, rs.getRow());
            //we don't store index in the entities, so we use Map with index as a key to sort dishes properly
            int ndx = rs.getInt("ndx");
            itemDishes.put(ndx, dish);
        }
        return allItemsDishes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue().values())));
    }


    @Override
    public void attachDishToMeal(long mealId, long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("mealId", mealId)
                .addValue("dishId", dishId);
        jdbcTemplate.update(INSERT_MEAL_DISH_LINK_SQL, parameters);
    }

    @Override
    public boolean detachDishFromMeal(long mealId, long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("mealId", mealId)
                .addValue("dishId", dishId);
        return jdbcTemplate.update(DELETE_MEAL_DISH_LINK_SQL, parameters) > 0;
    }

    @Override
    public void attachDishToDay(long dayId, long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("dayId", dayId)
                .addValue("dishId", dishId);
        jdbcTemplate.update(INSERT_DAY_DISH_LINK_SQL, parameters);
    }

    @Override
    public boolean detachDishFromDay(long dayId, long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("dayId", dayId)
                .addValue("dishId", dishId);
        return jdbcTemplate.update(DELETE_DAY_DISH_LINK_SQL, parameters) > 0;
    }

    @Override
    public List<Dish> getMealDishes(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("mealId", mealId);
        return jdbcTemplate.query(SELECT_MEAL_DISHES_SQL, parameters, this::mapRow);
    }

    @Override
    public List<Dish> getDayDishes(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        return jdbcTemplate.query(SELECT_DAY_DISHES_SQL, parameters, this::mapRow);
    }

    @Override
    public Map<Long, List<Dish>> getMealDishesForAllMealsInDay(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        return jdbcTemplate.query(SELECT_MEAL_DISHES_FOR_ALL_MEALS_IN_DAY_SQL, parameters, this::extractData);
    }

    @Override
    public Map<Long, List<Dish>> getMealDishesForAllMealsInPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("planId", planId);
        return jdbcTemplate.query(SELECT_MEAL_DISHES_FOR_ALL_MEALS_IN_PLAN_SQL, parameters, this::extractData);
    }

    @Override
    public Map<Long, List<Dish>> getDayDishesForAllDaysInPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("planId", planId);
        return jdbcTemplate.query(SELECT_DAY_DISHES_FOR_ALL_DAYS_IN_PLAN_SQL, parameters, this::extractData);
    }

    @Override
    public void deleteAllDishesForMeal(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("mealId", mealId);
        jdbcTemplate.update(DELETE_ALL_DISHES_FOR_MEAL_SQL, parameters);
    }

    @Override
    public void detachAllDishesFromMeal(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("mealId", mealId);
        jdbcTemplate.update(DELETE_ALL_DISHES_LINKS_FOR_MEAL_SQL, parameters);
    }

    @Override
    public void deleteAllDishesForDay(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        jdbcTemplate.update(DELETE_ALL_DISHES_FOR_DAY_SQL, parameters);
    }

    @Override
    public void detachAllDishesFromDay(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("dayId", dayId);
        jdbcTemplate.update(DELETE_ALL_DISHES_LINKS_FOR_DAY_SQL, parameters);
    }
}
