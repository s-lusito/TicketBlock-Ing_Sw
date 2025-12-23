package com.ticketblock.config;

import com.ticketblock.entity.Venue;
import com.ticketblock.entity.Wallet;
import com.ticketblock.repository.VenueRepository;
import com.ticketblock.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {  //CommandLineRunner permette di eseguire del codice all'avvio dell'applicazione, eseguendo il metodo run

    private final VenueRepository venueRepository;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final Web3j web3j;
    private final WalletRepository walletRepository;


    @Override
    @Transactional // Importante per la cascata di salvataggio
    public void run(String[] args) {

        if (venueRepository.count() == 0) {
            log.debug("Adding Venues to DB");
            List<Venue> venues = loadVenues();
            if(venues != null)
                venueRepository.saveAll(venues);
        } else {
            log.debug("Venues already exist. Loading from Json skipped");
        }


        try {
            List<String> accounts = web3j.ethAccounts().send().getAccounts();
            List<Wallet> walletList = new ArrayList<>();

            for (String account : accounts){
                walletList.add(
                        Wallet.builder()
                                .address(account)
                                .free(true)
                                .build()
                );
            }
            walletRepository.saveAll(walletList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




    }

    private List<Venue> loadVenues() {
        // Carica il file JSON come risorsa
        Resource resource = resourceLoader.getResource("classpath:data/venue_data.json");

        try (InputStream inputStream = resource.getInputStream()) {
            // legge gli elementi e li mappa in oggetti Venue
            List<Venue> venuesFromJSON = objectMapper.readValue(inputStream, new TypeReference<List<Venue>>() {
            });
            //come secondo parametro di readValue si mette il tipo ( se fosse una singola venue, Venue.class
            //Tuttavia avendo una lista, per indicare il tipo della lista di dati si crea una classe anonima, che mantiene l'informazione sul tipo del generico fra i suoi dati
            // senza typeReference non sarebbe possibile accedere al tipo dei dati visto che a runtime java effettua il type erasure ( List<Venue>.class darebbe solo l'info che è una List

            // Imposto manualmente le relazioni bidirezionali mancanti
            for (Venue venue : venuesFromJSON) {
                venue.getAddress().setVenue(venue);
                venue.getRows().forEach(row -> {
                    row.setVenue(venue);
                    row.getSeats().forEach(seat -> seat.setRow(row));
                });
            }
            log.debug("Venues loaded successfully");
            return venuesFromJSON;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
            return null;
    }



}