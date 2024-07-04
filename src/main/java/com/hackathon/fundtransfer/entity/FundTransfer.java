package com.hackathon.fundtransfer.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FUND_TRANSFER")
public class FundTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @NotBlank(message = "From Account should not be empty")
    private String fromAccount;

    @NotBlank(message = "To Account should not be empty")
    private String toAccount;

    @NotNull(message = "Amount should not be empty")
    private Double amount;

    private String comment;
    private LocalDateTime transactionDate;

    @ManyToOne
    private Customer customer;
}
