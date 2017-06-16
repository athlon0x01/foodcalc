package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

/**
 * Product Category repository implementation responsible for {@link ProductCategory} persistence.
 *
 * @author Anton Borovyk.
 */
@Repository
public class ProductCategoryRepo implements IProductCategoryRepo {

    static final String SELECT_ALL_CATEGORIES_SQL = "SELECT * FROM product_categories";
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
    public boolean addCategory(ProductCategory category) {
        return jdbcTemplate.update(INSERT_CATEGORY_SQL, new BeanPropertySqlParameterSource(category)) > 0;
    }

    @Override
    public boolean updateCategory(ProductCategory category) {
        return jdbcTemplate.update(UPDATE_CATEGORY_SQL, new BeanPropertySqlParameterSource(category)) > 0;
    }

    @Override
    public boolean deleteCategory(ProductCategory category) {
        return jdbcTemplate.update(DELETE_CATEGORY_SQL, new BeanPropertySqlParameterSource(category)) > 0;
    }
}
