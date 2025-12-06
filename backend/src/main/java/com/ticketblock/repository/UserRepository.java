package com.ticketblock.repository;

import com.ticketblock.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for User entity database operations.
 * 
 * Provides standard CRUD operations through JpaRepository and custom
 * query methods for finding users by email address.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    //Restituisce un Optional che conterrà l'utente se trovato, oppure sarà vuoto se non esiste.
}
