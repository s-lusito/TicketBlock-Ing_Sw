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
                "0xd32ebc7a6e22aa7e30445a7a2eed61fc54604e0446fde4401456b57256a2dfb0");
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
                "0xD9559326D171ed0E9aDf9cA13e799fcC712abB1d", // indirizzo del contratto deployato su Ganache
                web3j,
                credentials,
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit());
    }
}
