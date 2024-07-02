package com.hackathon.fundtransfer.service;

import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.exception.CustomException;
import com.hackathon.fundtransfer.exception.UnAuthorizedException;
import com.hackathon.fundtransfer.repository.AccountRepository;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final CustomerRepository customerRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * This method is used to create account for customer
     * @param username Customer Name
     * @param account Account details
     * @return Account details
     */
    public Account createAccountForCustomer(String username, Account account) {
        if (!account.getCustomer().getUsername().equals(username)) {
            throw new UnAuthorizedException("UnAuthorized Customer");
        }
        Optional<Account> checkAccountExist = accountRepository.findByAccountNumber(account.getAccountNumber());
        if (checkAccountExist.isPresent()) {
            throw new CustomException("Account Number already exists");
        }
        return accountRepository.save(account);
    }

    /**
     * This method is used to get the balance for customer
     * @param username Customer Name
     * @return Account details with balance amount
     */
    public List<Account> showBalance(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Customer Not Found"));
        return accountRepository.findByCustomerId(customer.getId());
    }
}
