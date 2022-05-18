package com.outdoor.foodcalc.domain.repository.meal;

import com.outdoor.foodcalc.domain.model.meal.MealType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link MealTypeRepo} class
 *
 * @author Olga Borovyk.
 */
public class MealTypeRepoTest {

    private static final Integer TYPE_ID = 12345;

    private static final String DUMMY_TYPE = "Dummy meal type";

    private static final MealType dummyType = new MealType(TYPE_ID, DUMMY_TYPE);

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private MealTypeRepo repo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getMealTypesTest() {
        List<MealType> expected = Collections.singletonList(dummyType);

        when(jdbcTemplate.query(MealTypeRepo.SELECT_ALL_MEALTYPES_SQL, repo)).thenReturn(expected);

        assertEquals(expected, repo.getMealTypes());

        verify(jdbcTemplate).query(MealTypeRepo.SELECT_ALL_MEALTYPES_SQL, repo);
    }

    @Test
    public void getMealTypeTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> TYPE_ID.equals(params.getValue("typeId"));

        when(jdbcTemplate.queryForObject(
                eq(MealTypeRepo.SELECT_MEALTYPE_SQL), argThat(matcher), Mockito.<RowMapper<MealType>>any()))
                .thenReturn(dummyType);

        Optional<MealType> actual = repo.getMealType(TYPE_ID);
        assertTrue(actual.isPresent());
        assertEquals(dummyType, actual.get());

        verify(jdbcTemplate).queryForObject(
                eq(MealTypeRepo.SELECT_MEALTYPE_SQL), argThat(matcher), Mockito.<RowMapper<MealType>>any());
    }

    @Test
    public void addMealTypeTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_TYPE.equals(params.getValue("name"));
        Integer expectedId = 54321;
        String[] keyColumns = new String[] {"id"};
        KeyHolder holder = mock(KeyHolder.class);
        MealTypeRepo spyRepo = spy(repo);

        when(holder.getKey()).thenReturn(expectedId);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate.update(eq(MealTypeRepo.INSERT_MEALTYPE_SQL), argThat(matcher),
                eq(holder), eq(keyColumns))).thenReturn(1);

        assertEquals(expectedId.intValue(), spyRepo.addMealType(dummyType));

        verify(holder).getKey();
        verify(jdbcTemplate).update(eq(MealTypeRepo.INSERT_MEALTYPE_SQL),
                argThat(matcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void addMealTypeFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_TYPE.equals(params.getValue("name"));
        int expectedId = -1;
        String[] keyColumns = new String[] {"id"};
        KeyHolder holder = mock(KeyHolder.class);
        MealTypeRepo spyRepo = spy(repo);

        when(holder.getKey()).thenReturn(null);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate.update(eq(MealTypeRepo.INSERT_MEALTYPE_SQL),
                argThat(matcher), eq(holder), eq(keyColumns))).thenReturn(0);

        assertEquals(expectedId, spyRepo.addMealType(dummyType));

        verify(holder).getKey();
        verify(jdbcTemplate).update(eq(MealTypeRepo.INSERT_MEALTYPE_SQL),
                argThat(matcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void updateMealTypeTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_TYPE.equals(params.getValue("name"))
                && TYPE_ID.equals(params.getValue("typeId"));
        when(jdbcTemplate.update(eq(MealTypeRepo.UPDATE_MEALTYPE_SQL), argThat(matcher))).thenReturn(1);

        assertTrue(repo.updateMealType(dummyType));

        verify(jdbcTemplate).update(eq(MealTypeRepo.UPDATE_MEALTYPE_SQL), argThat(matcher));
    }

    @Test
    public void updateMealTypeFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DUMMY_TYPE.equals(params.getValue("name"))
                && TYPE_ID.equals(params.getValue("typeId"));
        when(jdbcTemplate.update(eq(MealTypeRepo.UPDATE_MEALTYPE_SQL), argThat(matcher))).thenReturn(0);

        assertFalse(repo.updateMealType(dummyType));

        verify(jdbcTemplate).update(eq(MealTypeRepo.UPDATE_MEALTYPE_SQL), argThat(matcher));
    }

    @Test
    public void deleteMealTypeTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> TYPE_ID.equals(params.getValue("typeId"));
        when(jdbcTemplate.update(eq(MealTypeRepo.DELETE_MEALTYPE_SQL), argThat(matcher))).thenReturn(1);

        assertTrue(repo.deleteMealType(TYPE_ID));

        verify(jdbcTemplate).update(eq(MealTypeRepo.DELETE_MEALTYPE_SQL), argThat(matcher));
    }

    @Test
    public void deleteMealTypeFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> TYPE_ID.equals(params.getValue("typeId"));
        when(jdbcTemplate.update(eq(MealTypeRepo.DELETE_MEALTYPE_SQL), argThat(matcher))).thenReturn(0);

        assertFalse(repo.deleteMealType(TYPE_ID));

        verify(jdbcTemplate).update(eq(MealTypeRepo.DELETE_MEALTYPE_SQL), argThat(matcher));
    }

    @Test
    public void existsTest() {
        Integer expected = 1;
        ArgumentMatcher<SqlParameterSource> matcher = params -> TYPE_ID.equals(params.getValue("typeId"));
        when(jdbcTemplate.queryForObject(
                eq(MealTypeRepo.SELECT_MEALTYPE_EXISTS_SQL), argThat(matcher), eq(Integer.class)))
                .thenReturn(expected);

        assertTrue(repo.exist(TYPE_ID));

        verify(jdbcTemplate).queryForObject(
                eq(MealTypeRepo.SELECT_MEALTYPE_EXISTS_SQL), argThat(matcher), eq(Integer.class));
    }

    @Test
    public void existsNullTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> TYPE_ID.equals(params.getValue("typeId"));
        when(jdbcTemplate.queryForObject(
                eq(MealTypeRepo.SELECT_MEALTYPE_EXISTS_SQL), argThat(matcher), eq(Integer.class)))
                .thenReturn(null);

        assertFalse(repo.exist(TYPE_ID));

        verify(jdbcTemplate).queryForObject(
                eq(MealTypeRepo.SELECT_MEALTYPE_EXISTS_SQL), argThat(matcher), eq(Integer.class));
    }

    @Test
    public void mapRowTest() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(TYPE_ID);
        when(resultSet.getString("name")).thenReturn(DUMMY_TYPE);

        MealType actualType = repo.mapRow(resultSet, 2);
        assertEquals(dummyType, actualType);

        verify(resultSet).getInt("id");
        verify(resultSet).getString("name");
    }
}
