package com.ticketblock.repository;

import com.ticketblock.entity.Seat;
import com.ticketblock.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findFirstByFreeTrue();
}
