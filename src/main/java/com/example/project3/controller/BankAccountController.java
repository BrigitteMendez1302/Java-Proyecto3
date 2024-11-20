package com.example.project3.controller;

import com.example.project3.model.BankAccount;
import com.example.project3.service.BankAccountService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    /**
     * Creates a new bank account.
     *
     * @param headers     Request headers.
     * @param bankAccount The bank account details to be created.
     * @return A Mono containing the created bank account.
     */
    @PostMapping("/bank-accounts")
    public Mono<BankAccount> createBankAccount(
            @RequestHeader Map<String, String> headers,
            @RequestBody BankAccount bankAccount) {
        return bankAccountService.createBankAccount(bankAccount);
    }

    /**
     * Retrieves a bank account by its ID.
     *
     * @param headers Request headers.
     * @param id      The ID of the bank account.
     * @return A Mono containing the bank account if found.
     */
    @GetMapping("/bank-accounts/{id}")
    public Mono<BankAccount> getBankAccount(
            @RequestHeader Map<String, String> headers,
            @PathVariable String id) {
        return bankAccountService.getBankAccountById(id);
    }
}
