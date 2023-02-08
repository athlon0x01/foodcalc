package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.ProductRef;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductRefRepo} class
 *
 * @author Olga Borovyk
 */
public class ProductRefRepoTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final Long DISH_ID = 67890L;

    private static final ProductCategory dummyCategory =  new ProductCategory(12345L, "dummyCategory");

    private static final Product dummyProduct = new Product(
            54321L, "dummyProduct", "dummyDescr", dummyCategory,
            1.1f, 2.2f, 3.3f, 4.4f, 10);

    private static final Integer PRODUCT_REF_WEIGHT = 600;

    private static final ProductRef dummyProductRef = new ProductRef(dummyProduct, PRODUCT_REF_WEIGHT);

    private static final DishCategory dummyDishCategory = new DishCategory(1111L, "first dishes");

    private static final Dish dummyDish = new Dish(
            DISH_ID, "borsch", "dummyDescr",
            dummyDishCategory, Collections.EMPTY_LIST);

    private static final Dish dummyDishWithProducts = new Dish(
            DISH_ID, "borsch", "dummyDescr",
            dummyDishCategory, Collections.singletonList(dummyProductRef));

    private static final Map<Long, List<ProductRef>> allDishesWithProducts = new HashMap<>();
    static {
        allDishesWithProducts.put(DISH_ID, Collections.singletonList(dummyProductRef));
    }

    @Mock
    private ProductRepo productRepo;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    private ProductRefRepo repo;

    @Before
    public void setUp() throws Exception {
        repo = new ProductRefRepo(productRepo);
        ReflectionTestUtils.setField(repo, "jdbcTemplate", jdbcTemplate);
    }

    private static class MapSqlParameterSourceMatcher implements ArgumentMatcher<MapSqlParameterSource[]> {
        private final MapSqlParameterSource[] expected;

        public MapSqlParameterSourceMatcher(MapSqlParameterSource[] expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(MapSqlParameterSource[] actual) {
            if(expected.length == actual.length) {
                for(int i = 0; i < expected.length; i++) {
                    if(!expected[i].getValues().equals(actual[i].getValues())) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    @Test
    public void getAllDishProductsTest() {
        when(jdbcTemplate
                .query(eq(ProductRefRepo.SELECT_ALL_DISH_PRODUCTS_SQL),
                Mockito.<ResultSetExtractor<Map<Long, List<ProductRef>>>>any()
                ))
                .thenReturn(allDishesWithProducts);

        Map<Long, List<ProductRef>> actualMap = repo.getAllDishProducts();
        assertNotNull(actualMap);
        assertTrue(actualMap.containsKey(DISH_ID));
        assertEquals(1, actualMap.size());
        assertEquals(allDishesWithProducts.get(DISH_ID).size(), actualMap.get(DISH_ID).size());
        assertEquals(allDishesWithProducts.get(DISH_ID).get(0), actualMap.get(DISH_ID).get(0));

        verify(jdbcTemplate).query(eq(ProductRefRepo.SELECT_ALL_DISH_PRODUCTS_SQL),
                Mockito.<ResultSetExtractor<Map<Long, List<ProductRef>>>>any());
    }

    @Test
    public void addDishProductsTest() {
        int[] savedRows = new int[]{1};
        ArgumentMatcher<MapSqlParameterSource[]> paramsMatcher = new MapSqlParameterSourceMatcher(
                repo.getDishProductsSqlParameterSource(dummyDishWithProducts));
        when(jdbcTemplate.batchUpdate(
                eq(ProductRefRepo.INSERT_DISH_PRODUCTS_SQL), argThat(paramsMatcher)))
                .thenReturn(savedRows);

        assertTrue(repo.addDishProducts(dummyDishWithProducts));

        verify(jdbcTemplate).batchUpdate(eq(ProductRefRepo.INSERT_DISH_PRODUCTS_SQL), argThat(paramsMatcher));
    }

    @Test
    public void addDishWithoutProductsTest() {
        ArgumentMatcher<MapSqlParameterSource[]> paramsMatcher = new MapSqlParameterSourceMatcher(
                repo.getDishProductsSqlParameterSource(dummyDish));

        assertTrue(repo.addDishProducts(dummyDish));

        verify(jdbcTemplate, never()).batchUpdate(anyString(), argThat(paramsMatcher));
    }

    @Test
    public void addDishProductsFailTest() {
        int[] savedRows = new int[]{1, 2};
        ArgumentMatcher<MapSqlParameterSource[]> paramsMatcher = new MapSqlParameterSourceMatcher(
                repo.getDishProductsSqlParameterSource(dummyDishWithProducts));
        when(jdbcTemplate.batchUpdate(
                eq(ProductRefRepo.INSERT_DISH_PRODUCTS_SQL), argThat(paramsMatcher)))
                .thenReturn(savedRows);

        assertFalse(repo.addDishProducts(dummyDishWithProducts));

        verify(jdbcTemplate).batchUpdate(eq(ProductRefRepo.INSERT_DISH_PRODUCTS_SQL), argThat(paramsMatcher));
    }

    @Test
    public void getDishProductsTest() {
        List<ProductRef> expected = Collections.singletonList(dummyProductRef);
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dish"));
        when(jdbcTemplate.query(
                eq(ProductRefRepo.SELECT_DISH_PRODUCTS_SQL), argThat(matcher), Mockito.<RowMapper<ProductRef>>any()))
                .thenReturn(expected);

        List<ProductRef> actual = repo.getDishProducts(DISH_ID);
        assertEquals(expected, actual);

        verify(jdbcTemplate).query(
                eq(ProductRefRepo.SELECT_DISH_PRODUCTS_SQL), argThat(matcher), Mockito.<RowMapper<ProductRef>>any());
    }

    @Test
    public void deleteDishProductsTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dish"));
        when(jdbcTemplate.update(eq(ProductRefRepo.DELETE_DISH_PRODUCTS_SQL), argThat(matcher))).thenReturn(1);

        assertTrue(repo.deleteDishProducts(DISH_ID));

        verify(jdbcTemplate).update(eq(ProductRefRepo.DELETE_DISH_PRODUCTS_SQL), argThat(matcher));
    }

    @Test
    public void deleteDishProductsFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dish"));
        when(jdbcTemplate.update(eq(ProductRefRepo.DELETE_DISH_PRODUCTS_SQL), argThat(matcher))).thenReturn(0);

        assertFalse(repo.deleteDishProducts(DISH_ID));

        verify(jdbcTemplate).update(eq(ProductRefRepo.DELETE_DISH_PRODUCTS_SQL), argThat(matcher));
    }

    @Test
    public void ProductRefMapRowTest() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getInt("weight")).thenReturn(PRODUCT_REF_WEIGHT);
        when(productRepo.mapRow(eq(resultSet), anyInt())).thenReturn(dummyProduct);

        ProductRef actualProductRef = repo.mapRow(resultSet, 2);

        assertNotNull(actualProductRef);
        assertEquals(dummyProductRef.getWeight(), actualProductRef.getWeight(), 0.01);
        assertEquals(dummyProductRef.getProductId(), actualProductRef.getProductId());
        assertEquals(dummyProductRef.getName(), actualProductRef.getName());
        assertEquals(dummyProductRef.getProductCategoryName(), actualProductRef.getProductCategoryName());

        verify(productRepo).mapRow(eq(resultSet), anyInt());
        verify(resultSet).getInt("weight");
    }

    @Test
    public void extractDataTest() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);

        when(productRepo.mapRow(eq(resultSet), anyInt())).thenReturn(dummyProduct);
        when(resultSet.getLong("dish")).thenReturn(DISH_ID);
        when(resultSet.getInt("ndx")).thenReturn(1).thenReturn(0);
        when(resultSet.getInt("weight")).thenReturn(PRODUCT_REF_WEIGHT);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        Map<Long, List<ProductRef>> actualMap = repo.extractData(resultSet);
        assertNotNull(actualMap);
        assertTrue(actualMap.containsKey(DISH_ID));
        assertEquals(1, actualMap.size());
        assertEquals(2, actualMap.get(DISH_ID).size());
        assertEquals(allDishesWithProducts.get(DISH_ID).get(0), actualMap.get(DISH_ID).get(0));
        assertEquals(allDishesWithProducts.get(DISH_ID).get(0), actualMap.get(DISH_ID).get(1));

        verify(productRepo, times(2)).mapRow(eq(resultSet), anyInt());
        verify(resultSet, times(2)).getLong("dish");
        verify(resultSet, times(2)).getInt("weight");
    }
}
