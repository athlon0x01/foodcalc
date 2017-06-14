package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.RepositoryTestsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductRepo} class
 *
 * @author Anton Borovyk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestsConfig.class)
public class ProductRepoTest {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ProductRepo repo;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        ReflectionTestUtils.setField(repo, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    public void getAllProductsTest() throws Exception {
        Product dummyProduct = new Product(12345, "dummyProduct",
            new ProductCategory(11123, "dummyCategory"),
            1.1f, 3, 4.5f, 7, 11);
        List<Object> expected = Collections.singletonList(dummyProduct);

        when(jdbcTemplate.query(eq(ProductRepo.SELECT_ALL_PRODUCTS_SQL), any(RowMapper.class))).thenReturn(expected);

        List<Product> actual = repo.getAllProducts();
        assertEquals(expected, actual);

        verify(jdbcTemplate, times(1)).query(eq(ProductRepo.SELECT_ALL_PRODUCTS_SQL), any(RowMapper.class));
    }
}
