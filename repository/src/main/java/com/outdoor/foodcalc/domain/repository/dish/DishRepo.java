package com.outdoor.foodcalc.domain.repository.dish;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
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
import java.util.Optional;

@Repository
public class DishRepo extends AbstractRepository<Dish> implements IDishRepo, RowMapper<Dish> {

    static final String SELECT_ALL_DISHES_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName from dish d join dish_category c on d.category = c.id";
    static final String SELECT_DISH_SQL = "select d.id as dishId, d.name as dishName, d.description as description, " +
            "c.id as catId, c.name as catName from dish d join dish_category c on d.category = c.id where d.id = :dishId";
    static final String INSERT_DISH_SQL = "insert into dish (name, description, category) " +
            "values (:name, :description, :categoryId)";
    static final String UPDATE_DISH_SQL = "update dish set name = :name, description = :description, " +
            "category = :categoryId where id = :dishId";
    static final String DELETE_DISH_SQL = "delete from dish where id = :dishId";
    static final String SELECT_DISHES_COUNT_IN_CATEGORY_SQL = "select count(*)  from dish d join dish_category c " +
            "on d.category = c.id and c.id = :categoryId";
    static final String SELECT_DISH_EXIST_SQL = "select count(*) from dish where id = :dishId";

    @Override
    public List<Dish> getAllDishes() {
        return jdbcTemplate.query(SELECT_ALL_DISHES_SQL, this);
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
                .addValue("categoryId", dish.getCategory().getCategoryId());
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
                .category(category).build();
    }

    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
