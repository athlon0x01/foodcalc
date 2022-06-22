package com.outdoor.foodcalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * SpringBoot application
 *
 * @author Anton Borovyk.
 */
//TODO move Swagger to spring doc or open doc
@EnableWebMvc
@SpringBootApplication
public class FoodcalcApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodcalcApplication.class, args);
    }
}
