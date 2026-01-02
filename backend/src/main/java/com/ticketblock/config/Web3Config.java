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
                "0xa61aaa0a483cc4dbdd3375935f66d5eb074d3abebf0dc9ca830651e3b44d583a");
    }

    @Bean
    public ContractGasProvider gasProvider() {
        return new StaticGasProvider(
                BigInteger.valueOf(20_000_000_000L), // gasPrice
                BigInteger.valueOf(4_300_000) // gasLimit
        );

    }

    @Bean
    public TicketContract eventTicket(
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider gasProvider) {
        return TicketContract.load(
                "0x2D60E8c18371489d514Be8E5809A83B27ADC0636", // indirizzo del contratto deployato su Ganache
                web3j,
                credentials,
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit());
    }
}
