package com.hackathon.fundtransfer.service;

import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.entity.FundTransfer;
import com.hackathon.fundtransfer.exception.CustomException;
import com.hackathon.fundtransfer.exception.DetailsNotFoundException;
import com.hackathon.fundtransfer.exception.UnAuthorizedException;
import com.hackathon.fundtransfer.repository.AccountRepository;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import com.hackathon.fundtransfer.repository.FundTransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FundTransferServiceTest {

    @InjectMocks
    private FundTransferService fundTransferService;

    @Mock
    private FundTransferRepository fundTransferRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    private Customer customer;

    private FundTransfer fundTransfer;

    @BeforeEach
    public void setup() {
        customer = Customer.builder().id(12L).username("test").build();
        fundTransfer = FundTransfer.builder().transactionId(1L).fromAccount("118002").toAccount("988960")
                .amount(500.0).customer(customer).build();

    }

    @Test
    void transferFundsSuccess() throws Exception {
        String username = "test";

        Account fromAcc = Account.builder().accountId(1L).accountNumber("118002")
                .accountType("SAVINGS").balance(5000.0).customer(customer).build();

        Customer customerInfo = Customer.builder().id(13L).username("test1").build();

        Account toAcc = Account.builder().accountId(2L).accountNumber("988960")
                .accountType("SAVINGS").balance(3000.0).customer(customerInfo).build();

        when(this.accountRepository.findByAccountNumber(fundTransfer.getFromAccount()))
                .thenReturn(Optional.of(fromAcc));

        when(this.accountRepository.findByAccountNumber(fundTransfer.getToAccount()))
                .thenReturn(Optional.of(toAcc));

        fundTransfer.setTransactionId(1L);
        fundTransfer.setLocalDateTime(LocalDateTime.now());

        when(this.fundTransferRepository.save(fundTransfer)).thenReturn(fundTransfer);

        FundTransfer fundTransferInfo = fundTransferService.transferFunds(username, fundTransfer);

        assertEquals(fundTransferInfo.getTransactionId(), 1L);

    }

    @Test
    void transferFundsUnAuthorizedCustomer() {
        String username = "test2";
        Account fromAcc = Account.builder().accountId(1L).accountNumber("118002")
                .accountType("SAVINGS").balance(5000.0).customer(customer).build();

        Customer customerInfo = Customer.builder().id(13L).username("test1").build();

        Account toAcc = Account.builder().accountId(2L).accountNumber("988960")
                .accountType("SAVINGS").balance(3000.0).customer(customerInfo).build();

        when(this.accountRepository.findByAccountNumber(fundTransfer.getFromAccount()))
                .thenReturn(Optional.of(fromAcc));

        when(this.accountRepository.findByAccountNumber(fundTransfer.getToAccount()))
                .thenReturn(Optional.of(toAcc));

        assertThrows(UnAuthorizedException.class, () -> fundTransferService.transferFunds(username, fundTransfer));
    }

    @Test
    void transferFundsInsufficientFunds() {
        String username = "test";

        Account fromAcc = Account.builder().accountId(1L).accountNumber("118002")
                .accountType("SAVINGS").balance(400.0).customer(customer).build();

        Customer customerInfo = Customer.builder().id(13L).username("test1").build();

        Account toAcc = Account.builder().accountId(2L).accountNumber("988960")
                .accountType("SAVINGS").balance(3000.0).customer(customerInfo).build();

        when(this.accountRepository.findByAccountNumber(fundTransfer.getFromAccount()))
                .thenReturn(Optional.of(fromAcc));

        when(this.accountRepository.findByAccountNumber(fundTransfer.getToAccount()))
                .thenReturn(Optional.of(toAcc));

        fundTransfer.setTransactionId(1L);
        fundTransfer.setLocalDateTime(LocalDateTime.now());

        CustomException customException = assertThrows(CustomException.class,
                () -> fundTransferService.transferFunds(username, fundTransfer));

        assertEquals("Insufficient Funds to Transfer", customException.getMessage());

    }

    @Test
    void getTransactionsListSuccess() {
        List<FundTransfer> transactionsList = new ArrayList<>();
        String username = "test";

        Account account = Account.builder().accountId(1L).accountNumber("118002")
                .accountType("SAVINGS").balance(5000.0).customer(customer).build();

        when(this.customerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(customer));

        when(this.accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));

        fundTransfer.setTransactionId(1L);
        fundTransfer.setLocalDateTime(LocalDateTime.now());
        transactionsList.add(fundTransfer);

        when(this.fundTransferRepository.findByFromAccountOrToAccount(account.getAccountNumber(),
                account.getAccountNumber())).thenReturn(transactionsList);

        List<FundTransfer> transactionsList1 = fundTransferService.getTransactionsList(username, account.getAccountNumber());

        assertEquals(transactionsList1.size(), transactionsList.size());

    }

    @Test
    void getTransactionsListAccountNotFound() {
        String username = "test";

        when(this.customerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(customer));

        when(this.accountRepository.findByAccountNumber("118002")).thenReturn(Optional.empty());

        DetailsNotFoundException detailsNotFoundException = assertThrows(DetailsNotFoundException.class,
                () -> fundTransferService.getTransactionsList(username, "118002"));

        assertEquals("Account Details not Found", detailsNotFoundException.getMessage());
    }

    @Test
    void getTransactionsListUnAuthorized() {
        String username = "testuser";

        Customer customer1 = Customer.builder().id(2L).username("testuser").password("123456").build();

        Account account = Account.builder().accountId(1L).accountNumber("118002")
                .accountType("SAVINGS").balance(5000.0).customer(customer).build();

        when(this.customerRepository.findByUsername(username)).thenReturn(Optional.ofNullable(customer1));

        when(this.accountRepository.findByAccountNumber(account.getAccountNumber())).thenReturn(Optional.of(account));

        UnAuthorizedException unAuthorizedException = assertThrows(UnAuthorizedException.class,
                () -> fundTransferService.getTransactionsList(username, "118002"));
        assertEquals("UnAuthorized Request", unAuthorizedException.getMessage());
    }

    @Test
    void getTransactionsListCustomerNotFound() {
        String username = "test";

        when(this.customerRepository.findByUsername(username)).thenReturn(Optional.empty());

        CustomException customException = assertThrows(CustomException.class, () ->
                fundTransferService.getTransactionsList(username, "118002"));

        assertEquals("Customer Not Found", customException.getMessage());
    }

}
