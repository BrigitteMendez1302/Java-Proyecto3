package com.example.project3.service;

import com.example.project3.model.BankAccount;
import reactor.core.publisher.Mono;

/**
 * Service interface for managing bank accounts.
 * Defines the operations that can be performed on bank accounts.
 */
public interface BankAccountService {

    /**
     * Creates a new bank account.
     *
     * @param bankAccount The bank account to be created.
     * @return A Mono containing the created bank account.
     */
    Mono<BankAccount> createBankAccount(BankAccount bankAccount);

    /**
     * Retrieves a bank account by its ID.
     *
     * @param id The ID of the bank account to retrieve.
     * @return A Mono containing the bank account if found, or an empty Mono if not found.
     */
    Mono<BankAccount> getBankAccountById(String id);
}
