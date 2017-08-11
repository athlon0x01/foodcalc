package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.repository.RepositoryTestsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    private static class CategoryIdSqlParameterMatcher implements ArgumentMatcher<SqlParameterSource> {
        private long id;

        CategoryIdSqlParameterMatcher(long id) {
            this.id = id;
        }

        @Override
        public boolean matches(SqlParameterSource params) {
            return params.getValue("categoryId").equals(id);
        }
    }

    private static class CategorySqlParameterMatcher extends CategoryIdSqlParameterMatcher {
        private ProductCategory category;

        CategorySqlParameterMatcher(ProductCategory category) {
            super(category.getCategoryId());
            this.category = category;
        }

        @Override
        public boolean matches(SqlParameterSource params) {
            return super.matches(params) && category.getName().equals(params.getValue("name"));
        }
    }

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ProductCategoryRepo repo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
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
    public void getCategoryTest() throws Exception {
        List<Object> expected = Collections.singletonList(dummyCategory);
        CategoryIdSqlParameterMatcher idMatcher = new CategoryIdSqlParameterMatcher(CATEGORY_ID);

        when(jdbcTemplate.query(eq(ProductCategoryRepo.SELECT_CATEGORY_SQL), argThat(idMatcher), any(RowMapper.class))).thenReturn(expected);

        Optional<ProductCategory> actual = repo.getCategory(CATEGORY_ID);
        assertTrue(actual.isPresent());
        assertEquals(dummyCategory, actual.get());

        verify(jdbcTemplate, times(1)).query(eq(ProductCategoryRepo.SELECT_CATEGORY_SQL), argThat(idMatcher), any(RowMapper.class));
    }

    @Test
    public void getCategoryNotFountTest() throws Exception {
        CategoryIdSqlParameterMatcher idMatcher = new CategoryIdSqlParameterMatcher(CATEGORY_ID);

        when(jdbcTemplate.query(eq(ProductCategoryRepo.SELECT_CATEGORY_SQL), argThat(idMatcher), any(RowMapper.class))).thenReturn(Collections.emptyList());

        Optional<ProductCategory> actual = repo.getCategory(CATEGORY_ID);
        assertFalse(actual.isPresent());

        verify(jdbcTemplate, times(1)).query(eq(ProductCategoryRepo.SELECT_CATEGORY_SQL), argThat(idMatcher), any(RowMapper.class));
    }

    @Test
    public void addCategoryTest() throws Exception {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_CATEGORY.equals(params.getValue("name"));
        Long expectedId = 54321L;
        String[] keyColumns = new String[]{"id"};
        KeyHolder holder = mock(KeyHolder.class);
        ProductCategoryRepo spyRepo = spy(repo);

        when(holder.getKey()).thenReturn(expectedId);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate.update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL),
            argThat(matcher), eq(holder), eq(keyColumns))).thenReturn(1);

        assertEquals(expectedId.longValue(), spyRepo.addCategory(dummyCategory));

        verify(holder, times(1)).getKey();
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL),
            argThat(matcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void updateCategoryTest() throws Exception {
        CategorySqlParameterMatcher matcher = new CategorySqlParameterMatcher(dummyCategory);
        when(jdbcTemplate.update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher))).thenReturn(1);
        assertTrue(repo.updateCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void updateCategoryFailTest() throws Exception {
        CategorySqlParameterMatcher matcher = new CategorySqlParameterMatcher(dummyCategory);
        when(jdbcTemplate.update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher))).thenReturn(0);
        assertFalse(repo.updateCategory(dummyCategory));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void deleteCategoryTest() throws Exception {
        CategoryIdSqlParameterMatcher idMatcher = new CategoryIdSqlParameterMatcher(CATEGORY_ID);
        when(jdbcTemplate.update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(idMatcher))).thenReturn(1);
        assertTrue(repo.deleteCategory(CATEGORY_ID));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(idMatcher));
    }

    @Test
    public void deleteCategoryFailTest() throws Exception {
        CategoryIdSqlParameterMatcher idMatcher = new CategoryIdSqlParameterMatcher(CATEGORY_ID);
        when(jdbcTemplate.update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(idMatcher))).thenReturn(0);
        assertFalse(repo.deleteCategory(CATEGORY_ID));
        verify(jdbcTemplate, times(1)).update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(idMatcher));
    }
}