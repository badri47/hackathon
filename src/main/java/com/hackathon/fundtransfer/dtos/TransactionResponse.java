package com.hackathon.fundtransfer.dtos;

import com.hackathon.fundtransfer.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private String comment;
    private LocalDateTime transactionDate;
}
