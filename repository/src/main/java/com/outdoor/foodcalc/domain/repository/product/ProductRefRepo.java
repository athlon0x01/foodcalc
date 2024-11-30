package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.meal.Meal;
import com.outdoor.foodcalc.domain.model.plan.PlanDay;
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
import java.util.stream.Collectors;

/**
 * Product repository implementation responsible for {@link ProductRef} persistence.
 *
 * @author Olga Borovyk
 */
@Repository
public class ProductRefRepo extends AbstractRepository<ProductRef>
    implements IProductRefRepo, RowMapper<ProductRef>, ResultSetExtractor<Map<Long, List<ProductRef>>> {

    private final ProductRepo productRepo;


    static final String SELECT_ALL_TEMPLATE_DISHES_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.dish as itemId, dp.weight as weight, dp.package as packageId, dp.ndx as ndx " +
            "from dish_product dp  left join  dish d on dp.dish = d.id " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where d.template = true";

    static final String SELECT_DISHES_PRODUCTS_FOR_ALL_DAY_IN_PLAN_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.dish as itemId, dp.weight as weight, dp.package as packageId, dp.ndx as ndx " +
            "from dish_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.dish in (select day_dish.dish from day_dish " +
            "where day_dish.day in (select id from day_plan where plan = :planId))";

    static final String SELECT_DISHES_PRODUCTS_FOR_ALL_MEALS_IN_PLAN_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.dish as itemId, dp.weight as weight, dp.package as packageId, dp.ndx as ndx " +
            "from dish_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.dish in (select meal_dish.dish from meal_dish " +
            "where meal_dish.meal in (select day_meal.meal from day_meal where day_meal.day in (select id from day_plan where plan = :planId)))";

    static final String SELECT_DISHES_PRODUCTS_FOR_ALL_MEALS_IN_DAY_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.dish as itemId, dp.weight as weight, dp.package as packageId, dp.ndx as ndx " +
            "from dish_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.dish in (select meal_dish.dish from meal_dish " +
            "where meal_dish.meal in (select day_meal.meal from day_meal where day = :dayId))";

    static final String SELECT_DAY_DISHES_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.dish as itemId, dp.weight as weight, dp.package as packageId, dp.ndx as ndx " +
            "from dish_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.dish in (select day_dish.dish from day_dish where day_dish.day = :dayId)";

    static final String SELECT_MEAL_DISHES_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.dish as itemId, dp.weight as weight, dp.package as packageId, dp.ndx as ndx " +
            "from dish_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.dish in (select meal_dish.dish from meal_dish where meal_dish.meal = :mealId)";

    static final String SELECT_DISH_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, p.proteins as proteins, p.fats as fats, " +
            "p.carbs as carbs, p.defWeight as defWeight, dp.weight as weight, dp.package as packageId " +
            "from dish_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.dish = :dishId " +
            "order by dp.ndx";

    static final String INSERT_DISH_PRODUCTS_SQL = "insert into dish_product (dish, product, ndx, weight, package) " +
            "values (:dishId, :product, :ndx, :weight, :packageId)";

    static final String DELETE_DISH_PRODUCTS_SQL_COUNT = "with deleted as " +
            "(delete from dish_product where dish = :dishId returning *) " +
            "select count(*) from deleted";

    //------------------------------

    static final String SELECT_DAY_PRODUCTS_FOR_ALL_DAYS_IN_PLAN_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "dp.day as itemId, dp.weight as weight, dp.package as packageId, dp.ndx as ndx " +
            "from day_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.day in (select id from day_plan where plan = :planId)";

    static final String SELECT_DAY_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, p.proteins as proteins, p.fats as fats, " +
            "p.carbs as carbs, p.defWeight as defWeight, dp.weight as weight, dp.package as packageId " +
            "from day_product dp " +
            "left join product p on dp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where dp.day = :dayId " +
            "order by dp.ndx";

    static final String INSERT_DAY_PRODUCTS_SQL = "insert into day_product (day, product, ndx, weight, package) " +
            "values (:dayId, :product, :ndx, :weight, :packageId)";

    static final String DELETE_DAY_PRODUCTS_SQL_COUNT = "with deleted as " +
            "(delete from day_product where day = :dayId returning *) " +
            "select count(*) from deleted";

    //---------------------------

    static final String SELECT_MEAL_PRODUCTS_FOR_ALL_MEALS_IN_PLAN_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "mp.meal as itemId, mp.weight as weight, mp.package as packageId, mp.ndx as ndx " +
            "from meal_product mp " +
            "left join product p on mp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where mp.meal in (select day_meal.meal from day_meal where day in (select id from day_plan where plan = :planId))";

    static final String SELECT_MEAL_PRODUCTS_FOR_ALL_MEALS_IN_DAY_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, " +
            "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, " +
            "mp.meal as itemId, mp.weight as weight, mp.package as packageId, mp.ndx as ndx " +
            "from meal_product mp " +
            "left join product p on mp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where mp.meal in (select day_meal.meal from day_meal where day = :dayId)";

    static final String SELECT_MEAL_PRODUCTS_SQL = "select p.id as productId, p.name as productName, " +
            "c.id as catId, c.name as catName, p.calorific as calorific, p.proteins as proteins, p.fats as fats, " +
            "p.carbs as carbs, p.defWeight as defWeight, mp.weight as weight, mp.package as packageId " +
            "from meal_product mp " +
            "left join product p on mp.product  = p.id " +
            "left join product_category c on p.category = c.id " +
            "where mp.meal = :mealId " +
            "order by mp.ndx";

    static final String INSERT_MEAL_PRODUCTS_SQL = "insert into meal_product (meal, product, ndx, weight, package) " +
            "values (:mealId, :product, :ndx, :weight, :packageId)";

    static final String DELETE_MEAL_PRODUCTS_SQL_COUNT = "with deleted as " +
            "(delete from meal_product where meal = :mealId returning *) " +
            "select count(*) from deleted";

    static final String DELETE_ALL_DISH_PRODUCTS_FOR_MEAL_SQL = "delete from dish_product " +
            "where dish in (select md.dish from meal_dish md where md.meal = :mealId)";

    static final String DELETE_ALL_DISH_PRODUCTS_FOR_DAY_SQL = "delete from dish_product " +
            "where dish in (select dd.dish from day_dish dd where dd.day = :dayId)";


    @Autowired
    public ProductRefRepo(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public ProductRef mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = productRepo.mapRow(rs, rowNum);
        return new ProductRef(product,
                rs.getInt("weight"),
                rs.getLong("packageId"));
    }

    MapSqlParameterSource[] buildProductsInsertSqlParameterSource(String item, long itemId, List<ProductRef> products) {
        MapSqlParameterSource[] mappedArray = new MapSqlParameterSource[products.size()];
        for(int i = 0; i < products.size(); i++) {
            mappedArray[i] = new MapSqlParameterSource()
                    .addValue(item, itemId)
                    .addValue("product", products.get(i).getProductId())
                    .addValue("ndx", i)
                    .addValue("weight", products.get(i).getInternalWeight())
                    .addValue("packageId", products.get(i).getPackageId());
        }
        return mappedArray;
    }

    @Override
    public Map<Long, List<ProductRef>> getAllTemplateDishesProducts() {
        return jdbcTemplate.query(SELECT_ALL_TEMPLATE_DISHES_PRODUCTS_SQL, this::extractData);
    }

    @Override
    public List<ProductRef> getDishProducts(long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dishId", dishId);
        return jdbcTemplate.query(SELECT_DISH_PRODUCTS_SQL, parameters, this::mapRow);
    }

    @Override
    public boolean insertDishProducts(Dish dish) {
        if(dish.getProducts().isEmpty()) {
            return true;
        }
        int[] savedRows = jdbcTemplate.batchUpdate(INSERT_DISH_PRODUCTS_SQL,
                buildProductsInsertSqlParameterSource("dishId", dish.getDishId(), dish.getProducts()));
        return savedRows.length == dish.getProducts().size();
    }

    @Override
    public long deleteDishProducts(long dishId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dishId", dishId);
        Long count = jdbcTemplate.queryForObject(DELETE_DISH_PRODUCTS_SQL_COUNT, parameters, Long.class);
        return Optional.ofNullable(count).orElse(0L);
    }

    @Override
    public Map<Long, List<ProductRef>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, TreeMap<Integer, ProductRef>> allItemsProducts = new HashMap<>();
        while (rs.next()) {
            Long itemId = rs.getLong("itemId");
            //internal map to store products for some entity (dish \ meal \ day)
            TreeMap<Integer, ProductRef> itemProducts = allItemsProducts.computeIfAbsent(itemId, k -> new TreeMap<>());
            ProductRef productRef = mapRow(rs, rs.getRow());
            //we don't store index in ProjectRef, so we use Map with index as a key to sort product refs properly
            int ndx = rs.getInt("ndx");
            itemProducts.put(ndx, productRef);
        }
        return allItemsProducts.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue().values())));
    }

    @Override
    public Map<Long, List<ProductRef>> getDayProductsForAllDaysInPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("planId", planId);
        return jdbcTemplate.query(SELECT_DAY_PRODUCTS_FOR_ALL_DAYS_IN_PLAN_SQL, parameters, this::extractData);
    }

    @Override
    public List<ProductRef> getDayProducts(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        return jdbcTemplate.query(SELECT_DAY_PRODUCTS_SQL, parameters, this::mapRow);
    }

    @Override
    public boolean insertDayProducts(PlanDay day) {
        if(day.getProducts().isEmpty()) {
            return true;
        }
        int[] savedRows = jdbcTemplate.batchUpdate(INSERT_DAY_PRODUCTS_SQL,
                buildProductsInsertSqlParameterSource("dayId", day.getDayId(), day.getProducts()));
        return savedRows.length == day.getProducts().size();
    }

    @Override
    public long deleteDayProducts(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        Long count = jdbcTemplate.queryForObject(DELETE_DAY_PRODUCTS_SQL_COUNT, parameters, Long.class);
        return Optional.ofNullable(count).orElse(0L);
    }

    @Override
    public Map<Long, List<ProductRef>> getMealProductsForAllMealsInPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("planId", planId);
        return jdbcTemplate.query(SELECT_MEAL_PRODUCTS_FOR_ALL_MEALS_IN_PLAN_SQL, parameters, this::extractData);
    }

    @Override
    public Map<Long, List<ProductRef>> getMealProductsForAllMealsInDay(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        return jdbcTemplate.query(SELECT_MEAL_PRODUCTS_FOR_ALL_MEALS_IN_DAY_SQL, parameters, this::extractData);
    }

    @Override
    public List<ProductRef> getMealProducts(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("mealId", mealId);
        return jdbcTemplate.query(SELECT_MEAL_PRODUCTS_SQL, parameters, this::mapRow);
    }

    @Override
    public boolean insertMealProducts(Meal meal) {
        if(meal.getProducts().isEmpty()) {
            return true;
        }
        int[] savedRows = jdbcTemplate.batchUpdate(INSERT_MEAL_PRODUCTS_SQL,
                buildProductsInsertSqlParameterSource("mealId", meal.getMealId(), meal.getProducts()));
        return savedRows.length == meal.getProducts().size();
    }

    @Override
    public long deleteMealProducts(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("mealId", mealId);
        Long count = jdbcTemplate.queryForObject(DELETE_MEAL_PRODUCTS_SQL_COUNT, parameters, Long.class);
        return Optional.ofNullable(count).orElse(0L);
    }

    @Override
    public Map<Long, List<ProductRef>> getMealDishesProducts(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("mealId", mealId);
        return jdbcTemplate.query(SELECT_MEAL_DISHES_PRODUCTS_SQL, parameters, this::extractData);
    }

    @Override
    public Map<Long, List<ProductRef>> getDayDishesProducts(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        return jdbcTemplate.query(SELECT_DAY_DISHES_PRODUCTS_SQL, parameters, this::extractData);
    }

    @Override
    public Map<Long, List<ProductRef>> getDishesProductsForAllMealsInDay(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        return jdbcTemplate.query(SELECT_DISHES_PRODUCTS_FOR_ALL_MEALS_IN_DAY_SQL, parameters, this::extractData);
    }

    @Override
    public Map<Long, List<ProductRef>> getDishesProductsForAllMealsInPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("planId", planId);
        return jdbcTemplate.query(SELECT_DISHES_PRODUCTS_FOR_ALL_MEALS_IN_PLAN_SQL, parameters, this::extractData);
    }

    @Override
    public Map<Long, List<ProductRef>> getDishesProductsForAllDaysInPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("planId", planId);
        return jdbcTemplate.query(SELECT_DISHES_PRODUCTS_FOR_ALL_DAY_IN_PLAN_SQL, parameters, this::extractData);
    }

    @Override
    public void deleteAllDishProductsForMeal(long mealId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("mealId", mealId);
        jdbcTemplate.update(DELETE_ALL_DISH_PRODUCTS_FOR_MEAL_SQL, parameters);
    }

    @Override
    public void deleteAllDishProductsForDay(long dayId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("dayId", dayId);
        jdbcTemplate.update(DELETE_ALL_DISH_PRODUCTS_FOR_DAY_SQL, parameters);
    }
}
