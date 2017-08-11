package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * Product Category repository implementation responsible for {@link ProductCategory} persistence.
 *
 * @author Anton Borovyk.
 */
@Repository
public class ProductCategoryRepo implements IProductCategoryRepo {

    static final String SELECT_ALL_CATEGORIES_SQL = "SELECT * FROM product_categories";
    static final String SELECT_CATEGORY_SQL = "SELECT * FROM product_categories WHERE id = :categoryId";
    static final String INSERT_CATEGORY_SQL = "INSERT INTO product_categories (name) VALUES (:name)";
    static final String UPDATE_CATEGORY_SQL = "UPDATE product_categories SET name = :name WHERE id = :categoryId";
    static final String DELETE_CATEGORY_SQL = "DELETE FROM product_categories WHERE id = :categoryId";

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<ProductCategory> getCategories() {
        return jdbcTemplate.query(SELECT_ALL_CATEGORIES_SQL,
            (resultSet, rowNum) -> new ProductCategory(resultSet.getLong("id"), resultSet.getString("name")));
    }

    @Override
    public Optional<ProductCategory> getCategory(long id) {
        Optional<ProductCategory> result = Optional.empty();
        SqlParameterSource parameters = new MapSqlParameterSource() .addValue("categoryId", id);
        List<ProductCategory> categories = jdbcTemplate.query(SELECT_CATEGORY_SQL, parameters,
            (resultSet, rowNum) -> new ProductCategory(resultSet.getLong("id"), resultSet.getString("name")));
        if (!categories.isEmpty()) {
            result = Optional.of(categories.get(0));
        }
        return result;
    }

    @Override
    public long addCategory(ProductCategory category) {
        KeyHolder holder = getKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource() .addValue("name", category.getName());
        jdbcTemplate.update(INSERT_CATEGORY_SQL, parameters, holder, new String[]{"id"});
        return holder.getKey().longValue();
    }

    /**
     * Staff for unit testing
     */
    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }

    @Override
    public boolean updateCategory(ProductCategory category) {
        return jdbcTemplate.update(UPDATE_CATEGORY_SQL, new BeanPropertySqlParameterSource(category)) > 0;
    }

    @Override
    public boolean deleteCategory(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource() .addValue("categoryId", id);
        return jdbcTemplate.update(DELETE_CATEGORY_SQL, parameters) > 0;
    }
}
