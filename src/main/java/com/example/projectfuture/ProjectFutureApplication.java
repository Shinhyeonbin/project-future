package com.example.projectfuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class ProjectFutureApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectFutureApplication.class, args);
    }
}
