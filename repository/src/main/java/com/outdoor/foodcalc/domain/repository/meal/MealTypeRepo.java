package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.meal.MealType;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Meal type repository implementation responsible for {@link MealType} persistence.
 *
 * @author Olga Borovyk.
 */
@Repository
public class MealTypeRepo extends AbstractRepository<MealType>
        implements IMealTypeRepo, RowMapper<MealType> {

    static final String SELECT_ALL_MEALTYPES_SQL = "SELECT * FROM meal_type";
    static final String SELECT_MEALTYPE_SQL = "SELECT * FROM meal_type WHERE id = :typeId";
    static final String SELECT_MEALTYPE_EXISTS_SQL = "SELECT count(*) FROM meal_type WHERE id = :typeId";
    static final String INSERT_MEALTYPE_SQL = "INSERT INTO meal_type (name) VALUES (:name)";
    static final String UPDATE_MEALTYPE_SQL = "UPDATE meal_type SET name = :name WHERE id = :typeId";
    static final String DELETE_MEALTYPE_SQL = "DELETE FROM meal_type WHERE id = :typeId";

    @Override
    public List<MealType> getMealTypes() {
        return jdbcTemplate.query(SELECT_ALL_MEALTYPES_SQL, this);
    }

    @Override
    public Optional<MealType> getMealType(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("typeId", id);
        return getSingleObject(SELECT_MEALTYPE_SQL, parameters, this);
    }

    @Override
    public long addMealType(MealType mealType) {
        KeyHolder holder = getKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("name", mealType.getName());
        jdbcTemplate.update(INSERT_MEALTYPE_SQL, parameters, holder,new String[]{"id"});
        Number key = holder.getKey();
        return key != null ? key.intValue() : -1;
    }

    @Override
    public boolean updateMealType(MealType mealType) {
        return jdbcTemplate.update(UPDATE_MEALTYPE_SQL, new BeanPropertySqlParameterSource(mealType)) > 0;
    }

    @Override
    public boolean deleteMealType(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("typeId", id);
        return jdbcTemplate.update(DELETE_MEALTYPE_SQL, parameters) > 0;
    }

    @Override
    public boolean exist(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("typeId", id);
        Integer count = jdbcTemplate.queryForObject(SELECT_MEALTYPE_EXISTS_SQL, parameters, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public MealType mapRow(ResultSet resultSet, int i) throws SQLException {
        return new MealType(resultSet.getLong("id"), resultSet.getString("name"));
    }

    /**
     * Staff for unit testing
     */
    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
