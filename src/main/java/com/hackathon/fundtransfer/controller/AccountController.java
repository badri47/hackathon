package com.hackathon.fundtransfer.controller;

import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * This API is used to create Account for Customer
     * @param account account details
     * @param principal User details
     * @return Account information
     */
    @PostMapping("/")
    public ResponseEntity<Account> createAccountForCustomer(@Valid @RequestBody Account account,
                                                            Principal principal) {
        String username = principal.getName();
        return new ResponseEntity<>(accountService.createAccountForCustomer(username, account), HttpStatus.CREATED);
    }

    /**
     * This API is used to show balance of the account
     * @param principal User details
     * @return List of Accounts of user
     */
    @GetMapping("/showBalance")
    public ResponseEntity<List<Account>> showBalance(Principal principal) {
        String username = principal.getName();
        return new ResponseEntity<>(accountService.showBalance(username), HttpStatus.OK);
    }
}
