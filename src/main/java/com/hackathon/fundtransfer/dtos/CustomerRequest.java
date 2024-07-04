package com.hackathon.fundtransfer.dtos;

import jakarta.validation.constraints.*;
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

    @NotEmpty(message = "First Name should not be empty")
    private String firstName;

    @NotEmpty(message = "Last Name should not be empty")
    private String lastName;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Phone number should not be empty")
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$", message = "Phone number should be valid")
    private String phoneNumber;

    @NotBlank(message = "Account Number should not be empty")
    @Size(min = 5, message = "Account number length should be minimum 5")
    private String accountNumber;

    @NotBlank(message = "Account Type should not be empty")
    private String accountType;

    @NotNull(message = "Balance should not be empty")
    private Double balance;
}
