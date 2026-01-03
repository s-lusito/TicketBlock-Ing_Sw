package com.ticketblock;

import com.ticketblock.utils.TicketContract;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class TicketBlockApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBlockApplication.class, args);

    }
}
