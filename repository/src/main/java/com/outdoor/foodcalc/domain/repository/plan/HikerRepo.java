package com.outdoor.foodcalc.domain.repository.plan;

import com.outdoor.foodcalc.domain.model.plan.Hiker;
import com.outdoor.foodcalc.domain.repository.AbstractRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class HikerRepo extends AbstractRepository<Hiker> implements IHikerRepo, RowMapper<Hiker> {

    static final String SELECT_PLAN_HIKER_SQL = "select h.id as hikerId, h.name as name, h.description as description, h.weight_coef as wCoef " +
            "from hiker h " +
            "where plan = :planId";

    static final String SELECT_HIKER_SQL = "select h.id as hikerId, h.name as name, h.description as description, h.weight_coef as wCoef " +
            "from hiker h " +
            "where h.id = :hikerId";

    static final String INSERT_HIKER_SQL = "insert into hiker (name, description, weight_coef, plan) " +
            "values (:name, :description, :wCoef, :planId)";

    static final String UPDATE_HIKER_SQL = "update hiker set name = :name, description = :description, weight_coef = :wCoef " +
            "where id = :hikerId";

    static final String DELETE_HIKER_SQL = "delete from hiker where id = :hikerId";

    static final String SELECT_HIKER_EXIST_SQL = "select count(*) from hiker where id = :hikerId";

    @Override
    public List<Hiker> getPlanHikers(long planId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("planId", planId);
        return jdbcTemplate.query(SELECT_PLAN_HIKER_SQL, parameters, this);
    }

    @Override
    public Optional<Hiker> getHiker(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("hikerId", id);
        return getSingleObject(SELECT_HIKER_SQL, parameters, this);
    }

    MapSqlParameterSource getSqlParameterSource(Hiker hiker) {
        return new MapSqlParameterSource()
                .addValue("name", hiker.getName())
                .addValue("description", hiker.getDescription())
                .addValue("wCoef", hiker.getWeightCoefficient());
    }

    @Override
    public long addHiker(long planId, Hiker hiker) {
        KeyHolder holder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = getSqlParameterSource(hiker)
                .addValue("planId", planId);
        jdbcTemplate.update(INSERT_HIKER_SQL, parameters, holder, new String[] {"id"});
        Number key = holder.getKey();
        return key != null ? key.longValue() : -1L;
    }

    @Override
    public boolean updateHiker(Hiker hiker) {
        MapSqlParameterSource parameters = getSqlParameterSource(hiker)
                .addValue("hikerId", hiker.getId());
        return jdbcTemplate.update(UPDATE_HIKER_SQL, parameters) > 0;
    }

    @Override
    public void deleteHiker(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("hikerId", id);
        jdbcTemplate.update(DELETE_HIKER_SQL, parameters);
    }

    @Override
    public boolean existsHiker(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue("hikerId", id);
        Long count = jdbcTemplate.queryForObject(SELECT_HIKER_EXIST_SQL, parameters, Long.class);
        return count != null && count > 0;
    }

    @Override
    public Hiker mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Hiker.builder()
                .id(resultSet.getLong("hikerId"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .weightCoefficient(resultSet.getFloat("wCoef"))
                .build();
    }
}
