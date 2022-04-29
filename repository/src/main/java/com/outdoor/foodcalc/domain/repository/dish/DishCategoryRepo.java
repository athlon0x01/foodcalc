package com.outdoor.foodcalc.domain.repository.dish;

import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Product Category repository implementation responsible for {@link DishCategory} persistence.
 *
 * @author Olga Borovyk.
 */
@Repository
public class DishCategoryRepo extends AbstractRepository<DishCategory>
    implements IDishCategoryRepo, RowMapper<DishCategory> {

    static final String SELECT_ALL_CATEGORIES_SQL = "SELECT * FROM dish_category";
    static final String SELECT_CATEGORY_SQL = "SELECT * FROM dish_category WHERE id = :categoryId";
    static final String SELECT_CATEGORY_EXISTS_SQL = "SELECT count(*) FROM dish_category WHERE id = :categoryId";
    static final String INSERT_CATEGORY_SQL = "INSERT INTO dish_category (name) VALUES (:name)";
    static final String UPDATE_CATEGORY_SQL = "UPDATE dish_category SET name = :name WHERE id = :categoryId";
    static final String DELETE_CATEGORY_SQL = "DELETE FROM dish_category WHERE id = :categoryId";

    @Override
    public List<DishCategory> getCategories() {
        return jdbcTemplate.query(SELECT_ALL_CATEGORIES_SQL, this);
    }

    @Override
    public Optional<DishCategory> getCategory(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("categoryId", id);
        return getSingleObject(SELECT_CATEGORY_SQL, parameters, this);
    }

    @Override
    public long addCategory(DishCategory category) {
        KeyHolder holder = getKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("name", category.getName());
        jdbcTemplate.update(INSERT_CATEGORY_SQL, parameters, holder, new String[]{"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    @Override
    public boolean updateCategory(DishCategory category) {
        return jdbcTemplate.update(UPDATE_CATEGORY_SQL, new BeanPropertySqlParameterSource(category)) > 0;
    }

    @Override
    public boolean deleteCategory(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("categoryId", id);
        return jdbcTemplate.update(DELETE_CATEGORY_SQL, parameters) > 0;
    }

    @Override
    public boolean exist(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("categoryId", id);
        Long count = jdbcTemplate.queryForObject(SELECT_CATEGORY_EXISTS_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public DishCategory mapRow(ResultSet resultSet, int i) throws SQLException {
        return new DishCategory(resultSet.getLong("id"), resultSet.getString("name"));
    }

    /**
     * Staff for unit testing
     */
    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
