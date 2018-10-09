package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
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
 * Product Category repository implementation responsible for {@link ProductCategory} persistence.
 *
 * @author Anton Borovyk.
 */
@Repository
public class ProductCategoryRepo extends AbstractRepository<ProductCategory>
    implements IProductCategoryRepo, RowMapper<ProductCategory> {

    static final String SELECT_ALL_CATEGORIES_SQL = "SELECT * FROM product_categories";
    static final String SELECT_CATEGORY_SQL = "SELECT * FROM product_categories WHERE id = :categoryId";
    static final String INSERT_CATEGORY_SQL = "INSERT INTO product_categories (name) VALUES (:name)";
    static final String UPDATE_CATEGORY_SQL = "UPDATE product_categories SET name = :name WHERE id = :categoryId";
    static final String DELETE_CATEGORY_SQL = "DELETE FROM product_categories WHERE id = :categoryId";

    @Override
    public List<ProductCategory> getCategories() {
        return jdbcTemplate.query(SELECT_ALL_CATEGORIES_SQL, this);
    }

    @Override
    public Optional<ProductCategory> getCategory(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("categoryId", id);
        return getSingleObject(SELECT_CATEGORY_SQL, parameters, this);
    }

    @Override
    public long addCategory(ProductCategory category) {
        KeyHolder holder = getKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("name", category.getName());
        jdbcTemplate.update(INSERT_CATEGORY_SQL, parameters, holder, new String[]{"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    @Override
    public boolean updateCategory(ProductCategory category) {
        return jdbcTemplate.update(UPDATE_CATEGORY_SQL, new BeanPropertySqlParameterSource(category)) > 0;
    }

    @Override
    public boolean deleteCategory(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("categoryId", id);
        return jdbcTemplate.update(DELETE_CATEGORY_SQL, parameters) > 0;
    }

    @Override
    public ProductCategory mapRow(ResultSet resultSet, int i) throws SQLException {
        return new ProductCategory(resultSet.getLong("id"), resultSet.getString("name"));
    }

    /**
     * Staff for unit testing
     */
    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
