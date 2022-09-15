package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductRepo} class
 *
 * @author Anton Borovyk
 */
public class ProductRepoTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final Long CATEGORY_ID = 12345L;

    private static final Long PRODUCT_ID = 54321L;

    private static final ProductCategory dummyCategory =  new ProductCategory(CATEGORY_ID, "dummuCategory");

    private static final Product dummyProduct = new Product(
            PRODUCT_ID, "dummyProduct", "dummyDescr", dummyCategory,
            1.1f, 2.2f, 3.3f, 4.4f, 10);

    private ArgumentMatcher<MapSqlParameterSource> sqlParamsMatcher;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductRepo repo;

    @Before
    public void setUp() {
        sqlParamsMatcher = params -> params.getValues()
                .equals(repo.getSqlParameterSource(dummyProduct).getValues());
    }

    @Test
    public void getAllProductsTest() {
        List<Product> expected = Collections.singletonList(dummyProduct);
        when(jdbcTemplate.query(ProductRepo.SELECT_ALL_PRODUCTS_SQL, repo)).thenReturn(expected);

        List<Product> actual = repo.getAllProducts();
        assertEquals(expected, actual);

        verify(jdbcTemplate).query(ProductRepo.SELECT_ALL_PRODUCTS_SQL, repo);
    }

    @Test
    public void getProductTest() {
        Optional<Product> expected = Optional.of(dummyProduct);
        ArgumentMatcher<SqlParameterSource> matcher = params -> PRODUCT_ID.equals(params.getValue("productId"));
        when(jdbcTemplate.queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_SQL), argThat(matcher), Mockito.<RowMapper<Product>>any())).
                thenReturn(dummyProduct);

        Optional<Product> actual = repo.getProduct(PRODUCT_ID);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual);

        verify(jdbcTemplate).queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_SQL), argThat(matcher), Mockito.<RowMapper<Product>>any());
    }

    @Test
    public void addProductTest() {
        Long expectedId = 54321L;
        String[] keyColumns = new String[]{"id"};
        KeyHolder holder = mock(KeyHolder.class);
        ProductRepo spyRepo = spy(repo);
        when(holder.getKey()).thenReturn(expectedId);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate.update(eq(ProductRepo.INSERT_PRODUCT_SQL),
                argThat(sqlParamsMatcher), eq(holder), eq(keyColumns))).thenReturn(1);

        assertEquals(expectedId.longValue(), spyRepo.addProduct(dummyProduct));

        verify(holder).getKey();
        verify(jdbcTemplate).update(eq(ProductRepo.INSERT_PRODUCT_SQL),
                argThat(sqlParamsMatcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void addProductFailTest() {
        String[] keyColumns = new String[] {"id"};
        KeyHolder holder = mock(KeyHolder.class);
        ProductRepo spyRepo = spy(repo);

        when(holder.getKey()).thenReturn(null);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate.update(eq(ProductRepo.INSERT_PRODUCT_SQL),
                argThat(sqlParamsMatcher), eq(holder), eq(keyColumns))).thenReturn(1);

        assertEquals(-1L, spyRepo.addProduct(dummyProduct));

        verify(holder).getKey();
        verify(jdbcTemplate).update(eq(ProductRepo.INSERT_PRODUCT_SQL),
                argThat(sqlParamsMatcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void updateProductTest() {
        when(jdbcTemplate.update(eq(ProductRepo.UPDATE_PRODUCT_SQL), argThat(sqlParamsMatcher))).thenReturn(1);

        assertTrue(repo.updateProduct(dummyProduct));

        verify(jdbcTemplate).update(eq(ProductRepo.UPDATE_PRODUCT_SQL), argThat(sqlParamsMatcher));
    }

    @Test
    public void updateProductFailTest() {
        when(jdbcTemplate.update(eq(ProductRepo.UPDATE_PRODUCT_SQL), argThat(sqlParamsMatcher))).thenReturn(0);

        assertFalse(repo.updateProduct(dummyProduct));

        verify(jdbcTemplate).update(eq(ProductRepo.UPDATE_PRODUCT_SQL), argThat(sqlParamsMatcher));
    }

    @Test
    public void deleteProductTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> PRODUCT_ID.equals(params.getValue("productId"));
        when(jdbcTemplate.update(eq(ProductRepo.DELETE_PRODUCT_SQL), argThat(matcher))).thenReturn(1);

        assertTrue(repo.deleteProduct(PRODUCT_ID));

        verify(jdbcTemplate).update(eq(ProductRepo.DELETE_PRODUCT_SQL), argThat(matcher));
    }

    @Test
    public void deleteProductFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> PRODUCT_ID.equals(params.getValue("productId"));
        when(jdbcTemplate.update(eq(ProductRepo.DELETE_PRODUCT_SQL), argThat(matcher))).thenReturn(0);

        assertFalse(repo.deleteProduct(PRODUCT_ID));

        verify(jdbcTemplate).update(eq(ProductRepo.DELETE_PRODUCT_SQL), argThat(matcher));
    }

    @Test
    public void existsProductTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> PRODUCT_ID.equals(params.getValue("productId"));
        when(jdbcTemplate.queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_EXIST_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(1L);

        assertTrue(repo.existsProduct(PRODUCT_ID));

        verify(jdbcTemplate).queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_EXIST_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void existsProductNullTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> PRODUCT_ID.equals(params.getValue("productId"));
        when(jdbcTemplate.queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_EXIST_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(null);

        assertFalse(repo.existsProduct(PRODUCT_ID));

        verify(jdbcTemplate).queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_EXIST_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void existsProductZeroTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> PRODUCT_ID.equals(params.getValue("productId"));
        when(jdbcTemplate.queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_EXIST_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(0L);

        assertFalse(repo.existsProduct(PRODUCT_ID));

        verify(jdbcTemplate).queryForObject(
                eq(ProductRepo.SELECT_PRODUCT_EXIST_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void productsCountInCategoryTest() {
        Long expected = 1L;
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));

        when(jdbcTemplate.queryForObject(
            eq(ProductRepo.SELECT_PRODUCTS_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class)))
            .thenReturn(expected);

        assertEquals(expected.longValue(), repo.countProductsInCategory(CATEGORY_ID));

        verify(jdbcTemplate).queryForObject(
            eq(ProductRepo.SELECT_PRODUCTS_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void productsCountInCategoryNullTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));

        when(jdbcTemplate.queryForObject(
            eq(ProductRepo.SELECT_PRODUCTS_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class)))
            .thenReturn(null);

        assertEquals(0L, repo.countProductsInCategory(CATEGORY_ID));

        verify(jdbcTemplate).queryForObject(
            eq(ProductRepo.SELECT_PRODUCTS_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void mapRowTest() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("productId")).thenReturn(dummyProduct.getProductId());
        when(resultSet.getString("productName")).thenReturn(dummyProduct.getName());
        when(resultSet.getString("productDescription")).thenReturn(dummyProduct.getDescription());
        when(resultSet.getString("catName")).thenReturn(dummyProduct.getCategory().getName());
        when(resultSet.getLong("catId")).thenReturn(dummyProduct.getCategory().getCategoryId());
        when(resultSet.getFloat("calorific")).thenReturn(dummyProduct.getCalorific());
        when(resultSet.getFloat("proteins")).thenReturn(dummyProduct.getProteins());
        when(resultSet.getFloat("fats")).thenReturn(dummyProduct.getFats());
        when(resultSet.getFloat("carbs")).thenReturn(dummyProduct.getCarbs());
        when(resultSet.getInt("defWeight")).thenReturn(dummyProduct.getDefaultWeightInt());

        Product product = repo.mapRow(resultSet, 2);
        assertEquals(dummyProduct, product);

        verify(resultSet).getString("productName");
        verify(resultSet).getString("productDescription");
        verify(resultSet).getString("catName");
        verify(resultSet).getLong("productId");
        verify(resultSet).getLong("catId");
        verify(resultSet).getFloat("calorific");
        verify(resultSet).getFloat("proteins");
        verify(resultSet).getFloat("fats");
        verify(resultSet).getFloat("carbs");
        verify(resultSet).getInt("defWeight");
    }
}
