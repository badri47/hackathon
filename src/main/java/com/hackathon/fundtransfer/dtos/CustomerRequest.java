package com.hackathon.fundtransfer.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

    @NotEmpty(message = "UserName should not be empty")
    private String username;

    @NotEmpty(message = "Password should not be empty")
    private String password;

    @NotBlank(message = "Account Number should not be empty")
    @Size(min = 5, message = "Account number length should be minimum 5")
    private String accountNumber;

    @NotBlank(message = "Account Type should not be empty")
    private String accountType;

    @NotNull(message = "Balance should not be empty")
    private Double balance;
}
