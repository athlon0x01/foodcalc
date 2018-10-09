package com.outdoor.foodcalc.domain.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * Base class for all repositories
 *
 * @author Anton Borovyk.
 */
public abstract class AbstractRepository<T> {

    protected NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    protected Optional<T> getSingleObject(String sql, SqlParameterSource paramSource, RowMapper<T> rowMapper) {
        try {
            T object = jdbcTemplate.queryForObject(sql, paramSource, rowMapper);
            return object == null ? Optional.empty() : Optional.of(object);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
