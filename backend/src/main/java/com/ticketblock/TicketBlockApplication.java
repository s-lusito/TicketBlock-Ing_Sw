package com.ticketblock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the TicketBlock system.
 * 
 * This Spring Boot application manages ticket sales for events, including
 * venue management, event creation, ticket purchasing, and reselling functionality.
 * Scheduling is enabled for automated tasks such as updating event sale statuses.
 */
@SpringBootApplication
@EnableScheduling
public class TicketBlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBlockApplication.class, args);
    }

}
