package com.loglens;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point. @SpringBootApplication = "start Spring, scan this package for
 * @Controller/@Service/@Component classes, and auto-configure Tomcat + Thymeleaf".
 */
@SpringBootApplication
public class LogLensApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogLensApplication.class, args);
    }
}
