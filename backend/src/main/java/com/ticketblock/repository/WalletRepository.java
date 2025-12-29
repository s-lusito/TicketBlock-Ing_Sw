package com.ticketblock.repository;

import com.ticketblock.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findFirstByFreeTrue();

    double countWalletsByFreeTrue();
}
