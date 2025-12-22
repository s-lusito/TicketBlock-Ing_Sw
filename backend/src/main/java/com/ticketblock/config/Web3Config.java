package com.ticketblock.config;

import com.ticketblock.utils.TicketContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import java.math.BigInteger;

@Configuration
public class Web3Config {

    @Bean
    public Web3j web3j() {
        return Web3j.build(
                new HttpService("http://127.0.0.1:7545") // Ganache
        );
    }

    @Bean
    public Credentials credentials() {
        // Chiave privata di Ganache (SOLO ambiente locale)
        return Credentials.create(
            "0x3d3bde3e9e5872c23896435c8c41d24a82bf5ec131797eae75ec99b354ca2383"
        );
    }

    @Bean
    public ContractGasProvider gasProvider() {
        return new StaticGasProvider(
                BigInteger.valueOf(20_000_000_000L), // gasPrice
                BigInteger.valueOf(4_300_000)        // gasLimit
        );

    }

    @Bean
    public TicketContract eventTicket(
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider gasProvider
    ) {
        return TicketContract.load(
                "0x5d6218E3cf14677e123Ad600439220790Ad0095d", // indirizzo del contratto deployato su Ganache
                web3j,
                credentials,
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit()
        );
    }
}
