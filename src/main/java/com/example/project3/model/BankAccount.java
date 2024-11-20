package com.example.project3.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.codecs.pojo.annotations.BsonId;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "bank_accounts")
public class BankAccount {

    @BsonId
    private String id; // Unique identifier (e.g., account number)
    private String accountHolder; // Name of the account holder
    private Double balance; // Current account balance
}
