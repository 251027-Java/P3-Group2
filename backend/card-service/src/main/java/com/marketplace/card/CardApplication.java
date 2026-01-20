// Generated with assistance from Antigravity
// Reviewed and modified by Liam Ruiz
package com.marketplace.card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Main entry point for the Card Service application.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class CardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardApplication.class, args);
    }
}
