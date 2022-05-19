package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
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

/**
 * Product repository implementation responsible for {@link Product} persistence.
 *
 * @author Anton Borovyk
 */
@Repository
public class ProductRepo extends AbstractRepository<Product>
    implements IProductRepo, RowMapper<Product> {

    static final String SELECT_ALL_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
        "p.description as productDescription, c.id as catId, c.name as catName, p.calorific as calorific, " +
        "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight " +
        "from product p join product_category c on p.category = c.id";

    static final String SELECT_PRODUCT_SQL = "select p.id as productId, p.name as productName," +
            "p.description as productDescription, c.id as catId, c.name as catName, p.calorific as calorific" +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight " +
            "from product p join product_category c on p.category = c.id" +
            "where p.id = :productId";

    static final String INSERT_PRODUCT_SQL = "insert into product (name, description, category, calorific, " +
            "proteins, fats, carbs, defweight) values (:name, :description, :categoryId, :calorific," +
            ":proteins, :fats, :carbs, :defweight)";

    static final String UPDATE_PRODUCT_SQL = "update product set name = :name," +
            "description = :description, category = :categoryId, calorific = :calorific, proteins = :proteins," +
            "fats= :fats, carbs = :carbs, defweight = :defweight " +
            "where id = :productId";

    static final String DELETE_PRODUCT_SQL = "delete from product where id = :productId";

    static final String SELECT_PRODUCTS_COUNT_IN_CATEGORY_SQL = "select count(*) " +
        "from product p join product_category c on p.category = c.id and c.id = :categoryId";

    static final String SELECT_PRODUCT_EXIST_SQL = "select count(*) from product where id = :productId";

    @Override
    public List<Product> getAllProducts() {
        return jdbcTemplate.query(SELECT_ALL_PRODUCTS_SQL, this);
    }

    @Override
    public long countProductsInCategory(long categoryId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("categoryId", categoryId);
        Long productsCount = jdbcTemplate.queryForObject(SELECT_PRODUCTS_COUNT_IN_CATEGORY_SQL, parameters, Long.class);
        return productsCount == null ? 0 : productsCount;
    }

    @Override
    public Optional<Product> getProduct(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("productId", id);
        return getSingleObject(SELECT_PRODUCT_SQL, parameters, this);
    }

    @Override
    public long addProduct(Product product) {
        KeyHolder holder = getKeyHolder();
        MapSqlParameterSource parameters = getSqlParameterSource(product);
        jdbcTemplate.update(INSERT_PRODUCT_SQL, parameters, holder, new String[] {"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    MapSqlParameterSource getSqlParameterSource(Product product) {
        return new MapSqlParameterSource()
                .addValue("productId", product.getProductId())
                .addValue("name", product.getName())
                .addValue("description", product.getDescription())
                .addValue("categoryId", product.getCategory().getCategoryId())
                .addValue("calorific", product.getCalorific())
                .addValue("proteins", product.getProteins())
                .addValue("fats", product.getFats())
                .addValue("carbs", product.getCarbs())
                .addValue("defweight", product.getDefaultWeightInt());
    }

    @Override
    public boolean updateProduct(Product product) {
        MapSqlParameterSource parameters = getSqlParameterSource(product);
        return jdbcTemplate.update(UPDATE_PRODUCT_SQL, parameters) > 0;
    }

    @Override
    public boolean deleteProduct(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("productId", id);
        return jdbcTemplate.update(DELETE_PRODUCT_SQL, parameters) > 0;
    }

    @Override
    public boolean existsProduct(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("productId", id);
        Long count = jdbcTemplate.queryForObject(SELECT_PRODUCT_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {
        final ProductCategory category = new ProductCategory(resultSet.
                getLong("catId"), resultSet.getString("catName"));
        return new Product(resultSet.getLong("productId"),
            resultSet.getString("productName"),
            resultSet.getString("productDescription"),
            category,
            resultSet.getFloat("calorific"),
            resultSet.getFloat("proteins"),
            resultSet.getFloat("fats"),
            resultSet.getFloat("carbs"),
            resultSet.getInt("defWeight"));
    }

    KeyHolder getKeyHolder() {
        return new GeneratedKeyHolder();
    }
}
