package com.ecommerce.app;

import org.springframework.boot.SpringApplication;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * ECommerceApplication — main entry point for the Spring Boot backend.
 *
 * WHY these annotations:
 *
 * The main class lives in the sub-package 'com.ecommerce.app'. When
 * scanBasePackages is used, Spring Boot's three auto-scan mechanisms each lose
 * their default root and must each be told explicitly where to look:
 *
 * 1. @SpringBootApplication(scanBasePackages = "com.ecommerce") → Scans for
 * @Component, @Service, @Controller, @RestController beans across the entire
 * com.ecommerce.* package tree.
 *
 * 2. @EnableJpaRepositories(basePackages = "com.ecommerce.repository") → Tells
 * Spring Data JPA where to find repository interfaces and generate proxy
 * implementations for them (UserRepository, etc.)
 *
 * 3. @EntityScan(basePackages = "com.ecommerce.model") → Tells Hibernate where
 * to find @Entity classes (User, Product, etc.) Without this, Hibernate reports
 * "Not a managed type" because it only scans from the main class's own package
 * by default.
 *
 * All three are needed together whenever the main class is NOT at the root of
 * the package hierarchy.
 */
@SpringBootApplication(scanBasePackages = "com.ecommerce")
@EnableJpaRepositories(basePackages = "com.ecommerce.repository")
@EntityScan(basePackages = "com.ecommerce.model")
public class ECommerceApplication {

    public static void main(String[] args) {
        // Display company logo on startup
        printLogo();

        SpringApplication.run(ECommerceApplication.class, args);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ShopEase Backend is RUNNING on http://localhost:8080");
        System.out.println("  Hibernate auto-created/updated database tables.");
        System.out.println("  API Base URL: http://localhost:8080/api");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Displays the company ASCII art logo on startup. Requirements: "Display
     * the company logo in the console"
     */
    private static void printLogo() {
        System.out.println("\n");
        System.out.println("  ███████╗██╗  ██╗ ██████╗ ██████╗ ███████╗ █████╗ ███████╗███████╗");
        System.out.println("  ██╔════╝██║  ██║██╔═══██╗██╔══██╗██╔════╝██╔══██╗██╔════╝██╔════╝");
        System.out.println("  ███████╗███████║██║   ██║██████╔╝█████╗  ███████║███████╗█████╗  ");
        System.out.println("  ╚════██║██╔══██║██║   ██║██╔═══╝ ██╔══╝  ██╔══██║╚════██║██╔══╝  ");
        System.out.println("  ███████║██║  ██║╚██████╔╝██║     ███████╗██║  ██║███████║███████╗");
        System.out.println("  ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚══════╝╚═╝  ╚═╝╚══════╝╚══════╝");
        System.out.println("");
        System.out.println("              🛒  Your Premium Shopping Destination  🛒");
        System.out.println("  " + "─".repeat(65));
        System.out.println("  Initializing ShopEase E-Commerce Platform...");
        System.out.println("  Connecting to database and setting up Hibernate schema...\n");
    }
}
