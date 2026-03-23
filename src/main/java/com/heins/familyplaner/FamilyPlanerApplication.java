package com.heins.familyplaner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FamilyPlanerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyPlanerApplication.class, args);
    }

}
