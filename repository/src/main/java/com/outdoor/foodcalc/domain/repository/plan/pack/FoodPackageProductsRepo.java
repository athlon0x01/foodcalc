package com.outdoor.foodcalc.domain.repository.plan.pack;

import com.outdoor.foodcalc.domain.model.plan.pack.PackageDayProducts;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import com.outdoor.foodcalc.domain.repository.product.ProductRefRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Repository
public class FoodPackageProductsRepo implements IFoodPackageProductsRepo, ResultSetExtractor<Map<Long, Map<Long, PackageDayProducts>>> {

    private static final String SELECT_LINE1 = "select p.id as productId, p.name as productName, c.id as catId, c.name as catName, p.calorific as calorific, ";
    private static final String SELECT_LINE2 = "p.proteins as proteins, p.fats as fats, p.carbs as carbs, p.defWeight as defWeight, dp.day as date, ";
    private static final String LEFT_JOIN_PRODUCT_CATEGORY = "left join product_category c on p.category = c.id ";
    private static final String WHERE_DP_PLAN_PLAN_ID_UNION = "where dp.plan = :planId union ";
    static final String SELECT_ALL_PRODUCTS_FOR_PLAN_SQL = SELECT_LINE1 + SELECT_LINE2 +
            "day_product.weight as weight, day_product.package as packageId, day_product.day as dayId " +
            "from day_product left join product p on day_product.product = p.id " +
            LEFT_JOIN_PRODUCT_CATEGORY +
            "left join day_plan dp on dp.id = day_product.day\n" +
            WHERE_DP_PLAN_PLAN_ID_UNION +
            SELECT_LINE1 + SELECT_LINE2 +
            "   mp.weight as weight, mp.package as packageId, dm.day as dayId " +
            "from meal_product mp left join product p on mp.product = p.id " +
            LEFT_JOIN_PRODUCT_CATEGORY +
            "left join meal m on m.id = mp.meal " +
            "left join day_meal dm on m.id = dm.meal " +
            "left join day_plan dp on dp.id = dm.day " +
            WHERE_DP_PLAN_PLAN_ID_UNION +
            SELECT_LINE1 + SELECT_LINE2 +
            "   dish_product.weight as weight, dish_product.package as packageId, dd.day as dayId " +
            "from dish_product left join product p on dish_product.product = p.id " +
            LEFT_JOIN_PRODUCT_CATEGORY +
            "left join dish on dish_product.dish = dish.id " +
            "left join day_dish dd on dish.id = dd.dish " +
            "left join day_plan dp on dp.id = dd.day " +
            WHERE_DP_PLAN_PLAN_ID_UNION +
            SELECT_LINE1 + SELECT_LINE2 +
            "   dish_product.weight as weight, dish_product.package as packageId, dm.day as dayId " +
            "from dish_product left join product p on dish_product.product = p.id " +
            LEFT_JOIN_PRODUCT_CATEGORY +
            "left join dish on dish_product.dish = dish.id " +
            "left join meal_dish md on dish.id = md.dish " +
            "left join meal m on m.id = md.meal " +
            "left join day_meal dm on m.id = dm.meal " +
            "left join day_plan dp on dp.id = dm.day " +
            "where dp.plan = :planId";

    private NamedParameterJdbcTemplate jdbcTemplate;
    private final ProductRefRepo productRefRepo;

    public FoodPackageProductsRepo(ProductRefRepo productRefRepo) {
        this.productRefRepo = productRefRepo;
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Map<Long, Map<Long, PackageDayProducts>> getPackageProductsForPlan(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("planId", planId);
        return jdbcTemplate.query(SELECT_ALL_PRODUCTS_FOR_PLAN_SQL, parameters, this);
    }

    @Override
    public Map<Long, Map<Long, PackageDayProducts>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<Long, Map<Long, PackageDayProducts>> packages = new HashMap<>();
        while (resultSet.next()) {
            long packageId = resultSet.getLong("packageId");
            if (packageId > 0) {
                //null or 0 means that product hasn't been added to any package
                Map<Long, PackageDayProducts> packageDaysProducts = packages.computeIfAbsent(packageId, k -> new HashMap<>());
                long dayId = resultSet.getLong("dayId");
                LocalDate date = resultSet.getObject("date", LocalDate.class);
                ProductRef productRef = productRefRepo.mapRow(resultSet, resultSet.getRow());
                var packageDayProducts = packageDaysProducts.computeIfAbsent(dayId,
                        theId -> PackageDayProducts.builder()
                                .dayId(theId)
                                .date(date)
                                .build());
                packageDayProducts.getProducts().add(productRef);
            }
        }
        return packages;
    }
}
