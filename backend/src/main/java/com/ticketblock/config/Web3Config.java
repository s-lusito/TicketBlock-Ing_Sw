package com.ticketblock.config;

import com.ticketblock.utils.TicketContract;
import org.springframework.beans.factory.annotation.Value;
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
    public Web3j web3j(@Value("${blockchain.ganache-url}") String ganacheUrl) {
        return Web3j.build(
                new HttpService("http://host.docker.internal:7545") // Ganache
        );
    }

    @Bean
    public Credentials credentials(@Value("${blockchain.private-key}") String privateKey) {
        // Chiave privata di Ganache (SOLO ambiente locale)
        return Credentials.create(privateKey);
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
            @Value("${blockchain.contract-address}") String contractAddress,
            Web3j web3j,
            Credentials credentials,
            ContractGasProvider gasProvider) {
        return TicketContract.load(
                contractAddress, // indirizzo del contratto deployato su Ganache
                web3j,
                credentials,
                gasProvider.getGasPrice(),
                gasProvider.getGasLimit());
    }
}
