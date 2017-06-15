package com.outdoor.foodcalc.service;

import com.outdoor.foodcalc.domain.service.product.ProductCategoryDomainService;
import com.outdoor.foodcalc.domain.service.product.ProductDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import static org.mockito.Mockito.mock;

/**
 * Spring beans configuration for services unit tests
 *
 * @author Anton Borovyk.
 */
@Configuration
@ComponentScan(basePackages = "com.outdoor.foodcalc")
public class ServiceTestsConfig {

    @Bean
    @Primary
    public ProductDomainService productDomainService() {
        return mock(ProductDomainService.class);
    }

    @Bean
    @Primary
    public ProductCategoryDomainService productCategoryDomainService() {
        return mock(ProductCategoryDomainService.class);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return mock(DataSource.class);
    }
}
