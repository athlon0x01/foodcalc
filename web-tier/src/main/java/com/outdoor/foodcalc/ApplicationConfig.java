package com.outdoor.foodcalc;

import com.outdoor.foodcalc.domain.repository.RepositoryConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring application context
 *
 * @author Anton Borovyk.
 */
@Configuration
@Import(RepositoryConfig.class)
public class ApplicationConfig {
}
