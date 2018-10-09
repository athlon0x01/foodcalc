package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductRepo repo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(repo, "jdbcTemplate", jdbcTemplate);
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

        verify(resultSet, times(1)).getString("productName");
        verify(resultSet, times(1)).getString("catName");
        verify(resultSet, times(1)).getLong("productId");
        verify(resultSet, times(1)).getLong("catId");
        verify(resultSet, times(1)).getFloat("calorific");
        verify(resultSet, times(1)).getFloat("proteins");
        verify(resultSet, times(1)).getFloat("fats");
        verify(resultSet, times(1)).getFloat("carbs");
        verify(resultSet, times(1)).getFloat("defWeight");
    }

    @Test
    public void getAllProductsTest() {
        Product dummyProduct = mock(Product.class);
        List<Product> expected = Collections.singletonList(dummyProduct);

        when(jdbcTemplate.query(ProductRepo.SELECT_ALL_PRODUCTS_SQL, repo)).thenReturn(expected);

        List<Product> actual = repo.getAllProducts();
        assertEquals(expected, actual);

        verify(jdbcTemplate, times(1)).query(ProductRepo.SELECT_ALL_PRODUCTS_SQL, repo);
    }
}
