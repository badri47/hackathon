package com.hackathon.fundtransfer.service;

import com.hackathon.fundtransfer.dtos.CustomerRequest;
import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.repository.AccountRepository;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CustomerRequest customerRequest;

    @BeforeEach
    void setUp() {
        customerRequest = new CustomerRequest();
        customerRequest.setUsername("test");
        customerRequest.setPassword("123456");
        customerRequest.setAccountNumber("1119058");
        customerRequest.setAccountType("SAVINGS");
        customerRequest.setBalance(5000.0);
    }

    @Test
    void registerCustomerSuccess() {
        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setMessage("Customer created successfully with customer Id : "+1L);


        when(this.customerRepository.findByUsername(customerRequest.getUsername())).thenReturn(Optional.empty());

        Customer customer = new Customer();
        customer.setUsername(customerRequest.getUsername());
        customer.setPassword(passwordEncoder.encode(customerRequest.getPassword()));

        Customer customer1 = Customer.builder().id(1L).username(customerRequest.getUsername()).build();
        when(customerRepository.save(customer)).thenReturn(customer1);


        Account account = Account.builder().accountNumber("1119058").accountType("SAVINGS").balance(5000.0)
                .customer(customer1).build();
        when(this.accountRepository.findByAccountNumber(customerRequest.getAccountNumber())).thenReturn(Optional.empty());

        when(this.accountRepository.save(account)).thenReturn(account);
        PayloadResponse payloadResponse1 = customerService.registerCustomer(customerRequest);

        assertEquals(payloadResponse1.getMessage(), payloadResponse.getMessage());
    }

    @Test
    void registerCustomerAlreadyExists() {
        Customer customer = Customer.builder().username("test").password("123456").build();
        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setMessage("Customer already exists with username : "+customer.getUsername());

        Customer customerInfo= Customer.builder().id(12L).username("test").password("123456").build();
        when(this.customerRepository.findByUsername(customer.getUsername())).thenReturn(Optional.of(customerInfo));

        PayloadResponse payloadResponse1 = customerService.registerCustomer(customerRequest);

        assertEquals(payloadResponse1.getMessage(), payloadResponse.getMessage());
    }

    @Test
    void loadUserByUserNameTest() {
        Customer customer = Customer.builder().username("test").password("123456").build();

        when(this.customerRepository.findByUsername("test")).thenReturn(Optional.ofNullable(customer));

        UserDetails userDetails = customerService.loadUserByUsername("test");

        assertEquals(userDetails.getUsername(), "test");
    }


}
