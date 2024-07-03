package com.hackathon.fundtransfer.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private LocalDateTime localDateTime;
}
