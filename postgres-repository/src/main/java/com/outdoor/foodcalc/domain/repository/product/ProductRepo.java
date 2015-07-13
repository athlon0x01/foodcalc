package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
@Repository
public class ProductRepo implements IProductRepo {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<ProductCategory> getCategories() {
        final String sql = "select * from product_categories";
        return jdbcTemplate.query(sql,
                (resultSet, rowNum) -> new ProductCategory(resultSet.getInt("id"), resultSet.getString("name")));
    }

    @Override
    public List<Product> getAllProducts() {
        final String sql = "select p.id as productId, p.name as productName, c.id as catId, c.name as catName, p.calorific as calorific, " +
                "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight " +
                "from products p join product_categories c on p.category = c.id";
        return jdbcTemplate.query(sql, this::mapProducts);
    }

    private Product mapProducts(ResultSet resultSet, long currentRow) throws SQLException {
        final ProductCategory category = new ProductCategory(resultSet.getInt("catId"), resultSet.getString("catName"));
        return new Product(resultSet.getInt("productId"),
                resultSet.getString("productName"),
                category,
                resultSet.getFloat("calorific"),
                resultSet.getFloat("proteins"),
                resultSet.getFloat("fats"),
                resultSet.getFloat("carbs"),
                resultSet.getFloat("defWeight"));
    }
}
