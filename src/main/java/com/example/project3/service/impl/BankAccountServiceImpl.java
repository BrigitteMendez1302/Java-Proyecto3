package com.example.project3.service.impl;

import com.example.project3.model.BankAccount;
import com.example.project3.repository.BankAccountRepository;
import com.example.project3.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    /**
     * Creates a new bank account.
     *
     * @param bankAccount The bank account to be created.
     * @return A Mono containing the saved bank account.
     */
    @Override
    public Mono<BankAccount> createBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * Retrieves a bank account by its ID.
     *
     * @param id The ID of the bank account to retrieve.
     * @return A Mono containing the bank account if found, or an error if not found.
     * @throws ResponseStatusException If the bank account is not found.
     */
    @Override
    public Mono<BankAccount> getBankAccountById(String id) {
        return bankAccountRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found")));
    }
}
