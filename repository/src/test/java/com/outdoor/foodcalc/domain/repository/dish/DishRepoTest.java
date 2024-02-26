package com.outdoor.foodcalc.domain.repository.dish;

import com.outdoor.foodcalc.domain.model.dish.Dish;
import com.outdoor.foodcalc.domain.model.dish.DishCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * JUnit tests for {@link DishRepo} class
 *
 * @author Olga Borovyk
 */
@ExtendWith(MockitoExtension.class)
public class DishRepoTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private DishRepo repo;

    private ArgumentMatcher<MapSqlParameterSource> sqlParamsMatcher;

    private static final Long CATEGORY_ID = 12345L;

    private static final String CATEGORY_NAME = "dummy category";

    private static final DishCategory dummyCategory = new DishCategory(CATEGORY_ID, CATEGORY_NAME);

    private static final Long DISH_ID = 54321L;

    private static final Dish dummyDish = Dish.builder().dishId(DISH_ID).name("dummy dish").category(dummyCategory).build();

    @BeforeEach
    public void setUp() {
        sqlParamsMatcher = params -> params.getValues()
                .equals(repo.getSqlParameterSource(dummyDish).getValues());
    }

    @Test
    public void getAllDishesTest() {
        List<Dish> expected = Collections.singletonList(dummyDish);
        when(jdbcTemplate.query(DishRepo.SELECT_ALL_DISHES_SQL, repo)).thenReturn(expected);

        List<Dish> actual = repo.getAllTemplateDishes();
        assertEquals(expected, actual);

        verify(jdbcTemplate).query(DishRepo.SELECT_ALL_DISHES_SQL, repo);
    }

    @Test
    public void getDishTest() {
        Optional<Dish> expected = Optional.of(dummyDish);
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dishId"));
        when(jdbcTemplate
                .queryForObject(eq(DishRepo.SELECT_DISH_SQL), argThat(matcher), Mockito.<RowMapper<Dish>>any()))
                .thenReturn(dummyDish);

        Optional<Dish> actual = repo.getDish(DISH_ID);
        assertTrue(actual.isPresent());
        assertEquals(expected, actual);

        verify(jdbcTemplate)
                .queryForObject(eq(DishRepo.SELECT_DISH_SQL), argThat(matcher), Mockito.<RowMapper<Dish>>any());
    }

    @Test
    public void addDishTest() {
        Long expectedId = DISH_ID;
        String[] keyColumns = new String[] {"id"};
        KeyHolder holder = mock(KeyHolder.class);
        DishRepo spyRepo = spy(repo);
        when(holder.getKey()).thenReturn(expectedId);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate
                .update(eq(DishRepo.INSERT_DISH_SQL), argThat(sqlParamsMatcher), eq(holder), eq(keyColumns)))
                .thenReturn(1);

        assertEquals(expectedId.longValue(),spyRepo.addDish(dummyDish));

        verify(holder).getKey();
        verify(jdbcTemplate)
                .update(eq(DishRepo.INSERT_DISH_SQL), argThat(sqlParamsMatcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void addDishFailTest() {
        String[] keyColumns = new String[] {"id"};
        KeyHolder holder = mock(KeyHolder.class);
        DishRepo spyRepo = spy(repo);
        when(holder.getKey()).thenReturn(null);
        doReturn(holder).when(spyRepo).getKeyHolder();
        when(jdbcTemplate
                .update(eq(DishRepo.INSERT_DISH_SQL), argThat(sqlParamsMatcher), eq(holder), eq(keyColumns)))
                .thenReturn(1);

        assertEquals(-1L, spyRepo.addDish(dummyDish));

        verify(holder).getKey();
        verify(jdbcTemplate)
                .update(eq(DishRepo.INSERT_DISH_SQL), argThat(sqlParamsMatcher), eq(holder), eq(keyColumns));
    }

    @Test
    public void updateDishTest() {
        when(jdbcTemplate.update(eq(DishRepo.UPDATE_DISH_SQL), argThat(sqlParamsMatcher))).thenReturn(1);

        assertTrue(repo.updateDish(dummyDish));

        verify(jdbcTemplate).update(eq(DishRepo.UPDATE_DISH_SQL), argThat(sqlParamsMatcher));
    }

    @Test
    public void updateDishFailTest() {
        when(jdbcTemplate.update(eq(DishRepo.UPDATE_DISH_SQL), argThat(sqlParamsMatcher))).thenReturn(0);

        assertFalse(repo.updateDish(dummyDish));

        verify(jdbcTemplate).update(eq(DishRepo.UPDATE_DISH_SQL), argThat(sqlParamsMatcher));
    }

    @Test
    public void deleteDishTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dishId"));
        when(jdbcTemplate.update(eq(DishRepo.DELETE_DISH_SQL), argThat(matcher))).thenReturn(1);

        assertTrue(repo.deleteDish(DISH_ID));

        verify(jdbcTemplate).update(eq(DishRepo.DELETE_DISH_SQL), argThat(matcher));
    }

    @Test
    public void deleteDishFailTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dishId"));
        when(jdbcTemplate.update(eq(DishRepo.DELETE_DISH_SQL), argThat(matcher))).thenReturn(0);

        assertFalse(repo.deleteDish(DISH_ID));

        verify(jdbcTemplate).update(eq(DishRepo.DELETE_DISH_SQL), argThat(matcher));
    }

    @Test
    public void existDishTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dishId"));
        when(jdbcTemplate
                .queryForObject(eq(DishRepo.SELECT_DISH_EXIST_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(1L);

        assertTrue(repo.existsDish(DISH_ID));

        verify(jdbcTemplate).queryForObject(eq(DishRepo.SELECT_DISH_EXIST_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void existDishZeroTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dishId"));
        when(jdbcTemplate
                .queryForObject(eq(DishRepo.SELECT_DISH_EXIST_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(0L);

        assertFalse(repo.existsDish(DISH_ID));

        verify(jdbcTemplate).queryForObject(eq(DishRepo.SELECT_DISH_EXIST_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void existDishNullTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> DISH_ID.equals(params.getValue("dishId"));
        when(jdbcTemplate
                .queryForObject(eq(DishRepo.SELECT_DISH_EXIST_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(null);

        assertFalse(repo.existsDish(DISH_ID));

        verify(jdbcTemplate).queryForObject(eq(DishRepo.SELECT_DISH_EXIST_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void countDishesInCategoryTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));
        when(jdbcTemplate
                .queryForObject(eq(DishRepo.SELECT_DISHES_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(1L);

        assertEquals(1L, repo.countDishesInCategory(CATEGORY_ID));

        verify(jdbcTemplate)
                .queryForObject(eq(DishRepo.SELECT_DISHES_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void countDishesInCategoryNullTest() {
        ArgumentMatcher<SqlParameterSource> matcher = params -> CATEGORY_ID.equals(params.getValue("categoryId"));
        when(jdbcTemplate
                .queryForObject(eq(DishRepo.SELECT_DISHES_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class)))
                .thenReturn(null);

        assertEquals(0L, repo.countDishesInCategory(CATEGORY_ID));

        verify(jdbcTemplate)
                .queryForObject(eq(DishRepo.SELECT_DISHES_COUNT_IN_CATEGORY_SQL), argThat(matcher), eq(Long.class));
    }

    @Test
    public void mapRowTest() throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("dishId")).thenReturn(dummyDish.getDishId());
        when(resultSet.getString("dishName")).thenReturn(dummyDish.getName());
        when(resultSet.getLong("catId")).thenReturn(dummyDish.getCategory().getCategoryId());
        when(resultSet.getString("catName")).thenReturn(dummyDish.getCategory().getName());

        Dish actual = repo.mapRow(resultSet, 2);
        assertEquals(dummyDish, actual);

        verify(resultSet).getLong("dishId");
        verify(resultSet).getString("dishName");
        verify(resultSet).getLong("catId");
        verify(resultSet).getString("catName");
    }
}
