package com.hackathon.fundtransfer.service;


import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.exception.CustomException;
import com.hackathon.fundtransfer.exception.UnAuthorizedException;
import com.hackathon.fundtransfer.repository.AccountRepository;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    private Customer customer;

    private Account account;

    @BeforeEach
    public void setup() {
        customer = Customer.builder().id(12L).username("test").build();
        account = Account.builder().accountNumber("118852").accountType("SAVINGS").balance(100.05)
                .customer(customer).build();
    }

    @Test
    void createAccountSuccess() {
        String username = "test";

        when(this.accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.empty());

        when(this.accountRepository.save(account)).thenReturn(account);

        Account account1 = accountService.createAccountForCustomer(username,account);

        assertNotNull(account1);
        assertEquals(account1.getAccountNumber(), account.getAccountNumber());
    }

    @Test
    void createAccountFail() {
        String username = "test";
        account = new Account();
        account.setCustomer(customer);

        when(this.accountRepository.save(account)).thenReturn(account);

        Account account1 = accountService.createAccountForCustomer(username,account);

        assertThrows(NullPointerException.class, () ->  {
            throw new NullPointerException("Customer Id should not be null");
        });
    }

    @Test
    void createAccountUnAuthorizedCustomer() {
        String username = "testuser";

        assertThrows(UnAuthorizedException.class, () ->  {
            accountService.createAccountForCustomer(username,account);
        });
    }

    @Test
    void createAccountAlreadyExists() {
        String username = "test";

        when(this.accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertThrows(CustomException.class, () ->  {
            accountService.createAccountForCustomer(username,account);
        });
    }

    @Test
    void showBalanceSuccess() {
        String username = "test";

        List<Account> accountList = Collections.singletonList(account);
        when(this.customerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(customer));

        when(this.accountRepository.findByCustomerId(customer.getId())).thenReturn(accountList);

        List<Account> accountList1 = accountService.showBalance(username);

        assertEquals(accountList1.size(), 1);
    }

    @Test
    void showBalanceFail() {
        String username = "";

        customer = new Customer();
        account = new Account();
        List<Account> accountList = Collections.singletonList(account);
        when(this.customerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(customer));

        when(this.accountRepository.findByCustomerId(customer.getId())).thenReturn(accountList);

        List<Account> accountList1 = accountService.showBalance(username);

        assertNull(accountList1.get(0).getAccountNumber());
    }
}
