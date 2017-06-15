package com.outdoor.foodcalc.domain.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Spring Configuration for DataSource and other repository related beans.
 *
 * @author Anton Borovyk.
 */
@Configuration
@ComponentScan(basePackages = "com.outdoor.foodcalc")
public class RepositoryConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/foodcalc");
        dataSource.setUsername("postgres");
        dataSource.setPassword("ABCabc123");

        return dataSource;
    }
}
