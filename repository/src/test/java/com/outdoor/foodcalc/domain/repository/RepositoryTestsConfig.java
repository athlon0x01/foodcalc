package com.outdoor.foodcalc.domain.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

/**
 * Spring beans configuration for unit tests
 *
 * @author Anton Borovyk.
 */
@Configuration
@ComponentScan(basePackages = "com.outdoor.foodcalc")
public class RepositoryTestsConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        return mock(DataSource.class);
    }
}
