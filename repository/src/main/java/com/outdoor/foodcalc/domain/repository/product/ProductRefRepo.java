package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Product repository implementation responsible for {@link ProductRef} persistence.
 *
 * @author Olga Borovyk
 */
@Repository
public class ProductRefRepo extends AbstractRepository<ProductRef>
    implements IProductRefRepo, RowMapper<ProductRef>, ResultSetExtractor<Map<Long, List<ProductRef>>> {

    private final ProductRepo productRepo;


    static final String SELECT_ALL_DISH_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "p.description as productDescription, c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.dish as dish, dp.weight as weight, dp.ndx as ndx " +
            "from dish_product dp " +
            "join product p on dp.product  = p.id " +
            "join product_category c on p.category = c.id ";

    static final String SELECT_DISH_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "p.description as productDescription, c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, dp.weight as weight " +
            "from dish_product dp " +
            "join product p on dp.product  = p.id " +
            "join product_category c on p.category = c.id " +
            "where dp.dish = :dish " +
            "order by dp.ndx";

    static final String INSERT_DISH_PRODUCTS_SQL = "insert into dish_product (dish, product, ndx, weight) " +
            "values (:dish, :product, :ndx, :weight)";

    static final String DELETE_DISH_PRODUCTS_SQL_COUNT = "with deleted as " +
            "(delete from dish_product where dish = :dish returning *) " +
            "select count(*) from deleted";


    @Autowired
    public ProductRefRepo(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public ProductRef mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = productRepo.mapRow(rs, rowNum);
        return new ProductRef(product, rs.getInt("weight"));
    }

    MapSqlParameterSource[] getDishProductsSqlParameterSource(Dish dish) {
        MapSqlParameterSource[] mappedArray = new MapSqlParameterSource[dish.getProducts().size()];
        for(int i = 0; i < dish.getProducts().size(); i++) {
            mappedArray[i] = new MapSqlParameterSource()
                    .addValue("dish", dish.getDishId())
                    .addValue("product", dish.getProducts().get(i).getProductId())
                    .addValue("ndx", i)
                    .addValue("weight", dish.getProducts().get(i).getInternalWeight());
        }
        return mappedArray;
    }

    @Override
    public Map<Long, List<ProductRef>> getAllDishProducts() {
        return jdbcTemplate.query(SELECT_ALL_DISH_PRODUCTS_SQL, this::extractData);
    }

    @Override
    public List<ProductRef> getDishProducts(long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dish", dishId);
        return jdbcTemplate.query(SELECT_DISH_PRODUCTS_SQL, parameters, this::mapRow);
    }

    @Override
    public boolean addDishProducts(Dish dish) {
        if(dish.getProducts().isEmpty()) {
            return true;
        }
        int[] savedRows = jdbcTemplate.batchUpdate(
                INSERT_DISH_PRODUCTS_SQL, getDishProductsSqlParameterSource(dish));
        return savedRows.length == dish.getProducts().size();
    }

    @Override
    public long deleteDishProducts(long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dish", dishId);
        Long count = jdbcTemplate.queryForObject(DELETE_DISH_PRODUCTS_SQL_COUNT, parameters, Long.class);
        return Optional.ofNullable(count).orElse(0L);
    }

    @Override
    public Map<Long, List<ProductRef>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, TreeMap<Integer, ProductRef>> allDishesProducts = new HashMap<>();
        while (rs.next()) {
            Long dishId = rs.getLong("dish");
            int ndx = rs.getInt("ndx");
            ProductRef dishProduct = mapRow(rs, rs.getRow());
            TreeMap<Integer, ProductRef> dishProducts = allDishesProducts.computeIfAbsent(dishId, k -> new TreeMap<>());
            dishProducts.put(ndx, dishProduct);
        }
        Map<Long, List<ProductRef>> resultMap = new HashMap<>();
        allDishesProducts.forEach((k, v) -> resultMap.put(k, new ArrayList<>(v.values())));
        return resultMap;
    }
}
