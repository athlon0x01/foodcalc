package com.outdoor.foodcalc.domain.repository.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * JUnit tests for {@link ProductCategoryRepo} class
 *
 * @author Anton Borovyk.
 */
public class ProductCategoryRepoTest {


    private static final Long CATEGORY_ID = 12345L;
    private static final String DUMMY_CATEGORY = "Dummy category";

    private static final ProductCategory dummyCategory = new ProductCategory(CATEGORY_ID, DUMMY_CATEGORY);

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private ProductCategoryRepo repo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCategoriesTest() {
        List<ProductCategory> expected = Collections.singletonList(dummyCategory);

        when(jdbcTemplate.query(ProductCategoryRepo.SELECT_ALL_CATEGORIES_SQL, repo)).thenReturn(expected);

        List<ProductCategory> actual = repo.getCategories();
        assertEquals(expected, actual);

        verify(jdbcTemplate).query(ProductCategoryRepo.SELECT_ALL_CATEGORIES_SQL, repo);
    }

    @Test
    public void getCategoryTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));
        when(jdbcTemplate.queryForObject(
            eq(ProductCategoryRepo.SELECT_CATEGORY_SQL), argThat(matcher), Mockito.<RowMapper<ProductCategory>>any()))
            .thenReturn(dummyCategory);

        Optional<ProductCategory> actual = repo.getCategory(CATEGORY_ID);
        assertTrue(actual.isPresent());
        assertEquals(dummyCategory, actual.get());

        verify(jdbcTemplate).queryForObject(
            eq(ProductCategoryRepo.SELECT_CATEGORY_SQL), argThat(matcher), Mockito.<RowMapper<ProductCategory>>any());
    }

    @Test
    public void addCategoryTest() {
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

        verify(holder).getKey();
        verify(jdbcTemplate).update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL),
            argThat(matcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void addCategoryFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_CATEGORY.equals(params.getValue("name"));
        String[]  keyColumns = new String[] {"id"};
        KeyHolder holder = mock(KeyHolder.class);
        ProductCategoryRepo spyRepo = spy(repo);

        when(holder.getKey()).thenReturn(null);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate.update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL),
                argThat(matcher), eq(holder), eq(keyColumns))).thenReturn(0);

        assertEquals(-1L, spyRepo.addCategory(dummyCategory));

        verify(holder).getKey();
        verify(jdbcTemplate).update(eq(ProductCategoryRepo.INSERT_CATEGORY_SQL),
                argThat(matcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void updateCategoryTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_CATEGORY.equals(params.getValue("name"))
            && CATEGORY_ID.equals(params.getValue("categoryId"));
        when(jdbcTemplate.update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher))).thenReturn(1);

        assertTrue(repo.updateCategory(dummyCategory));

        verify(jdbcTemplate)
            .update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void updateCategoryFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_CATEGORY.equals(params.getValue("name"))
            && CATEGORY_ID.equals(params.getValue("categoryId"));
        when(jdbcTemplate.update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher))).thenReturn(0);

        assertFalse(repo.updateCategory(dummyCategory));

        verify(jdbcTemplate)
            .update(eq(ProductCategoryRepo.UPDATE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void deleteCategoryTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));
        when(jdbcTemplate.update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher))).thenReturn(1);

        assertTrue(repo.deleteCategory(CATEGORY_ID));

        verify(jdbcTemplate)
            .update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void deleteCategoryFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));
        when(jdbcTemplate.update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher))).thenReturn(0);

        assertFalse(repo.deleteCategory(CATEGORY_ID));

        verify(jdbcTemplate)
            .update(eq(ProductCategoryRepo.DELETE_CATEGORY_SQL), argThat(matcher));
    }

    @Test
    public void existsTest() {
        Long expected = 1L;
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));

        when(jdbcTemplate.queryForObject(
                eq(ProductCategoryRepo.SELECT_CATEGORY_EXISTS_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(expected);

        assertTrue(repo.exist(CATEGORY_ID));

        verify(jdbcTemplate).queryForObject(
                eq(ProductCategoryRepo.SELECT_CATEGORY_EXISTS_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void existsNullTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));

        when(jdbcTemplate.queryForObject(
                eq(ProductCategoryRepo.SELECT_CATEGORY_EXISTS_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(null);

        assertFalse(repo.exist(CATEGORY_ID));

        verify(jdbcTemplate).queryForObject(
                eq(ProductCategoryRepo.SELECT_CATEGORY_EXISTS_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void existsZeroTest() {
        Long expected = 0L;
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));

        when(jdbcTemplate.queryForObject(
                eq(ProductCategoryRepo.SELECT_CATEGORY_EXISTS_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(expected);

        assertFalse(repo.exist(CATEGORY_ID));

        verify(jdbcTemplate).queryForObject(
                eq(ProductCategoryRepo.SELECT_CATEGORY_EXISTS_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void mapRowTest() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getString("name")).thenReturn(DUMMY_CATEGORY);
        when(resultSet.getLong("id")).thenReturn(CATEGORY_ID);

        ProductCategory category = repo.mapRow(resultSet, 2);
        assertEquals(dummyCategory, category);

        verify(resultSet).getString("name");
        verify(resultSet).getLong("id");
    }
}
