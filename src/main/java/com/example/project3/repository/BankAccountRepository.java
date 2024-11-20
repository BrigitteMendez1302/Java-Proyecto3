package com.example.project3.repository;

import com.example.project3.model.BankAccount;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends ReactiveMongoRepository<BankAccount, String> {
}
