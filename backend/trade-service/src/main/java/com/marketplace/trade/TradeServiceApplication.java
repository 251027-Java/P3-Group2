// Generated with assistance from Cursor
// Reviewed and modified by Matt Selle
package com.marketplace.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for Trade Service
 * Handles trade requests and management for marketplace platform
 *
 * Note: @EnableEurekaClient is no longer needed in Spring Boot 4.0+
 * Eureka client is auto-configured when spring-cloud-starter-netflix-eureka-client is on the classpath
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class TradeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeServiceApplication.class, args);
    }
}
