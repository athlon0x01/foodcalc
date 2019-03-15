package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductRepo} class
 *
 * @author Anton Borovyk
 */
public class ProductRepoTest {

    private static final Long CATEGORY_ID = 12345L;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductRepo repo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void mapRowTest() throws SQLException {
        long productId = 12345;
        final String productName = "dummyProduct";
        long categoryId = 11123;
        String categoryName = "dummyCategory";
        float calorific = 1.1f;
        float proteins = 3.3f;
        float fats = 4.5f;
        float carbs = 7.3f;
        float defaultWeight = 11.1f;
        Product dummyProduct = new Product(productId, productName,
            new ProductCategory(categoryId, categoryName),
            calorific, proteins, fats, carbs, defaultWeight);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("productName")).thenReturn(productName);
        when(resultSet.getString("catName")).thenReturn(categoryName);
        when(resultSet.getLong("productId")).thenReturn(productId);
        when(resultSet.getLong("catId")).thenReturn(categoryId);
        when(resultSet.getFloat("calorific")).thenReturn(calorific);
        when(resultSet.getFloat("proteins")).thenReturn(proteins);
        when(resultSet.getFloat("fats")).thenReturn(fats);
        when(resultSet.getFloat("carbs")).thenReturn(carbs);
        when(resultSet.getFloat("defWeight")).thenReturn(defaultWeight);

        Product product = repo.mapRow(resultSet, 2);
        assertEquals(dummyProduct, product);

        verify(resultSet).getString("productName");
        verify(resultSet).getString("catName");
        verify(resultSet).getLong("productId");
        verify(resultSet).getLong("catId");
        verify(resultSet).getFloat("calorific");
        verify(resultSet).getFloat("proteins");
        verify(resultSet).getFloat("fats");
        verify(resultSet).getFloat("carbs");
        verify(resultSet).getFloat("defWeight");
    }

    @Test
    public void getAllProductsTest() {
        Product dummyProduct = mock(Product.class);
        List<Product> expected = Collections.singletonList(dummyProduct);

        when(jdbcTemplate.query(ProductRepo.SELECT_ALL_PRODUCTS_SQL, repo)).thenReturn(expected);

        List<Product> actual = repo.getAllProducts();
        assertEquals(expected, actual);

        verify(jdbcTemplate).query(ProductRepo.SELECT_ALL_PRODUCTS_SQL, repo);
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
}
