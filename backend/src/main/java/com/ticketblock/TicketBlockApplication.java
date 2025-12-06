package com.ticketblock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TicketBlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBlockApplication.class, args);
    }

}
