package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Product repository implementation responsible for {@link Product} persistence.
 *
 * @author Anton Borovyk
 */
@Repository
public class ProductRepo extends AbstractRepository<Product>
    implements IProductRepo, RowMapper<Product> {

    static final String SELECT_ALL_PRODUCTS_SQL = "select p.id as productId, p.name as productName, c.id as catId, c.name as catName, p.calorific as calorific, " +
        "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight " +
        "from products p join product_categories c on p.category = c.id";

    @Override
    public List<Product> getAllProducts() {
        return jdbcTemplate.query(SELECT_ALL_PRODUCTS_SQL, this);
    }

    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {
        final ProductCategory category = new ProductCategory(resultSet.getLong("catId"), resultSet.getString("catName"));
        return new Product(resultSet.getLong("productId"),
            resultSet.getString("productName"),
            category,
            resultSet.getFloat("calorific"),
            resultSet.getFloat("proteins"),
            resultSet.getFloat("fats"),
            resultSet.getFloat("carbs"),
            resultSet.getFloat("defWeight"));
    }
}
