package com.rudra.financemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Personal Finance Manager application.
 * This class boots the Spring Boot framework and runs the embedded web server.
 */
@SpringBootApplication
public class FinanceManagerApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(FinanceManagerApplication.class, args);
    }

}
