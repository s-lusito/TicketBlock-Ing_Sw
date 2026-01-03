package com.ticketblock.repository;

import com.ticketblock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    User findByUserId(Integer userId);
    //Restituisce un Optional che conterrà l'utente se trovato, oppure sarà vuoto se non esiste.
}
