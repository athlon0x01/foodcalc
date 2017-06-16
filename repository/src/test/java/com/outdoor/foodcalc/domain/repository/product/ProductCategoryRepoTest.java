package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.RepositoryTestsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link ProductCategoryRepo} class
 *
 * @author Anton Borovyk.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RepositoryTestsConfig.class)
public class ProductCategoryRepoTest {


    private static final long CATEGORY_ID = 12345;
    private static final String DUMMY_CATEGORY = "Dummy category";

    private static final ProductCategory dummyCategory = new ProductCategory(CATEGORY_ID, DUMMY_CATEGORY);

    private static final class CategorySqlParameterMatcher implements ArgumentMatcher<SqlParameterSource> {
        @Override
        public boolean matches(SqlParameterSource params) {
            return params.getValue("categoryId").equals(CATEGORY_ID)
                && DUMMY_CATEGORY.equals(params.getValue("name"));
        }
    }

    private static final CategorySqlParameterMatcher matcher = new CategorySqlParameterMatcher();

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ProductCategoryRepo repo;

    @Before
    public void setUp() throws Exception {
        jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        ReflectionTestUtils.setField(repo, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    public void getCategoriesTest() throws Exception {
        List<Object> expected = Collections.singletonList(dummyCategory);

        when(jdbcTemplate.query(eq(ProductCategoryRepo.SELECT_ALL_CATEGORIES_SQL), any(RowMapper.class))).thenReturn(expected);

        List<ProductCategory> actual = repo.getCategories();
        assertEquals(expected, actual);

        verify(jdbcTemplate, times(1)).query(eq(ProductCategoryRepo.SELECT_ALL_CATEGORIES_SQL), any(RowMapper.class));
    }

    @Test
    public void addCategoryTest() throws Exception {
        when(jdbcTemplate.update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL), argThat(matcher))).thenReturn(1);
        assertTrue(repo.addCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void addCategoryFailTest() throws Exception {
        when(jdbcTemplate.update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL), argThat(matcher))).thenReturn(0);
        assertFalse(repo.addCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void updateCategoryTest() throws Exception {
        when(jdbcTemplate.update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher))).thenReturn(1);
        assertTrue(repo.updateCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void updateCategoryFailTest() throws Exception {
        when(jdbcTemplate.update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher))).thenReturn(0);
        assertFalse(repo.updateCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        when(jdbcTemplate.update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher))).thenReturn(1);
        assertTrue(repo.deleteCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void deleteCategoryFailTest() throws Exception {
        when(jdbcTemplate.update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher))).thenReturn(0);
        assertFalse(repo.deleteCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher));
    }
}