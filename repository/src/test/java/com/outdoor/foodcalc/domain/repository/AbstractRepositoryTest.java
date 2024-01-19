package com.outdoor.foodcalc.domain.repository;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests fro {@link AbstractRepository} class.
 *
 * @author Anton Borovyk.
 */
public class AbstractRepositoryTest {

    private static final String SQL = "select * from w";
    private static final ProductCategory DUMMY_CATEGORY = new ProductCategory(12345L, "dummy");
    private static final SqlParameterSource PARAMETERS = new MapSqlParameterSource();

    //class for testing
    private final static class CategoryRepo extends AbstractRepository<ProductCategory> {
    }

    @Mock
    private DataSource dataSource;

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Mock
    private RowMapper<ProductCategory> rowMapper;

    @InjectMocks
    private CategoryRepo repo;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(repo, "jdbcTemplate", jdbcTemplate);
    }

    @Test
    public void getSingleObjectTest() {
        when(jdbcTemplate.queryForObject(SQL, PARAMETERS, rowMapper)).thenReturn(DUMMY_CATEGORY);

        Optional<ProductCategory> actual = repo.getSingleObject(SQL, PARAMETERS, rowMapper);
        assertTrue(actual.isPresent());
        assertEquals(DUMMY_CATEGORY, actual.get());

        verify(jdbcTemplate, times(1)).queryForObject(SQL, PARAMETERS, rowMapper);
    }

    @Test
    public void getSingleObjectNullTest() {
        when(jdbcTemplate.queryForObject(SQL, PARAMETERS, rowMapper)).thenReturn(null);

        Optional<ProductCategory> actual = repo.getSingleObject(SQL, PARAMETERS, rowMapper);
        assertFalse(actual.isPresent());

        verify(jdbcTemplate, times(1)).queryForObject(SQL, PARAMETERS, rowMapper);
    }

    @Test
    public void getSingleObjectExceptionTest() {
        when(jdbcTemplate.queryForObject(SQL, PARAMETERS, rowMapper)).thenThrow(new EmptyResultDataAccessException(0));

        Optional<ProductCategory> actual = repo.getSingleObject(SQL, PARAMETERS, rowMapper);
        assertFalse(actual.isPresent());
    }

    @Test
    public void setDataSourceTest() {
        CategoryRepo testRepo = new CategoryRepo();
        testRepo.setDataSource(dataSource);
        Object template = ReflectionTestUtils.getField(testRepo, "jdbcTemplate");
        assertNotNull(template);
        assertTrue(template instanceof NamedParameterJdbcTemplate);
    }
}