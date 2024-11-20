package com.example.project3.service.impl;

import com.example.project3.model.Transaction;
import com.example.project3.model.TransactionType;
import com.example.project3.repository.BankAccountRepository;
import com.example.project3.repository.TransactionRepository;
import com.example.project3.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;

    /**
     * Creates and saves a deposit transaction while updating the account balance.
     *
     * @param accountId The ID of the account where the deposit will be made.
     * @param amount    The amount to deposit.
     * @return A Mono containing the saved deposit transaction.
     */
    @Override
    public Mono<Transaction> deposit(String accountId, Double amount) {
        return bankAccountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
                .flatMap(account -> {
                    account.setBalance(account.getBalance() + amount); // Update the balance
                    return bankAccountRepository.save(account); // Save the updated account
                })
                .flatMap(updatedAccount -> {
                    Transaction transaction = Transaction.builder()
                            .type(TransactionType.DEPOSIT)
                            .amount(amount)
                            .date(LocalDateTime.now())
                            .destinationAccountId(accountId)
                            .build();
                    return transactionRepository.save(transaction); // Save the transaction
                });
    }

    /**
     * Creates and saves a withdrawal transaction while updating the account balance.
     *
     * @param accountId The ID of the account from which the withdrawal will be made.
     * @param amount    The amount to withdraw.
     * @return A Mono containing the saved withdrawal transaction.
     */
    @Override
    public Mono<Transaction> withdraw(String accountId, Double amount) {
        return bankAccountRepository.findById(accountId) // Fetch the account by ID
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found")))
                .flatMap(account -> {
                    // Check if the account has enough balance
                    if (account.getBalance() < amount) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance"));
                    }
                    // Deduct the amount from the account's balance
                    account.setBalance(account.getBalance() - amount);
                    return bankAccountRepository.save(account); // Save the updated account
                })
                .flatMap(updatedAccount -> {
                    // Create a withdrawal transaction
                    Transaction transaction = Transaction.builder()
                            .type(TransactionType.WITHDRAWAL)
                            .amount(amount)
                            .date(LocalDateTime.now())
                            .sourceAccountId(accountId)
                            .build();
                    return transactionRepository.save(transaction); // Save the transaction
                });
    }


    /**
     * Creates and saves a transfer transaction between two accounts while updating their balances.
     *
     * @param sourceAccountId      The ID of the account from which the transfer will be made.
     * @param destinationAccountId The ID of the account to which the transfer will be made.
     * @param amount               The amount to transfer.
     * @return A Mono containing the saved transfer transaction.
     */
    @Override
    public Mono<Transaction> transfer(String sourceAccountId, String destinationAccountId, Double amount) {
        return bankAccountRepository.findById(sourceAccountId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found")))
                .flatMap(sourceAccount -> {
                    if (sourceAccount.getBalance() < amount) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance"));
                    }
                    sourceAccount.setBalance(sourceAccount.getBalance() - amount); // Deduct from source
                    return bankAccountRepository.save(sourceAccount);
                })
                .flatMap(updatedSourceAccount -> bankAccountRepository.findById(destinationAccountId)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination account not found")))
                        .flatMap(destinationAccount -> {
                            destinationAccount.setBalance(destinationAccount.getBalance() + amount); // Add to destination
                            return bankAccountRepository.save(destinationAccount);
                        })
                        .flatMap(updatedDestinationAccount -> {
                            Transaction transaction = Transaction.builder()
                                    .type(TransactionType.TRANSFER)
                                    .amount(amount)
                                    .date(LocalDateTime.now())
                                    .sourceAccountId(sourceAccountId)
                                    .destinationAccountId(destinationAccountId)
                                    .build();
                            return transactionRepository.save(transaction); // Save the transaction
                        }));
    }

    /**
     * Retrieves the global transaction history, sorted by date in descending order.
     *
     * @return A Flux containing all transactions, sorted by date in descending order.
     */
    @Override
    public Flux<Transaction> getGlobalTransactionHistory() {
        return transactionRepository.findAllByOrderByDateDesc();
    }

    /**
     * Retrieves the transaction history for a specific account.
     * Includes transactions where the account is either the source or the destination.
     *
     * @param accountId The ID of the account whose transactions are to be retrieved.
     * @return A Flux containing all transactions related to the specified account, sorted by date in descending order.
     */
    @Override
    public Flux<Transaction> getAccountTransactionHistory(String accountId) {
        return transactionRepository.findBySourceAccountIdOrDestinationAccountIdOrderByDateDesc(accountId, accountId);
    }
}
