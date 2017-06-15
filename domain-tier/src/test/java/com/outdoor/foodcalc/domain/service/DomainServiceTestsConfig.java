package com.outdoor.foodcalc.domain.service;

import com.outdoor.foodcalc.domain.repository.product.IProductCategoryRepo;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Spring beans configuration for Domain services unit tests
 *
 * @author Anton Borovyk.
 */
@Configuration
@ComponentScan(basePackages = "com.outdoor.foodcalc")
public class DomainServiceTestsConfig {

    @Bean
    @Primary
    public IProductRepo productRepo() {
        return mock(IProductRepo.class);
    }

    @Bean
    @Primary
    public IProductCategoryRepo productCategoryRepo() {
        return mock(IProductCategoryRepo.class);
    }
}
