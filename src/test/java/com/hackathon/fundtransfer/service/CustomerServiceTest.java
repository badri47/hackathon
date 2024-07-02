package com.hackathon.fundtransfer.service;

import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void registerCustomerSuccess() {
        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setMessage("Customer created successfully with customer Id : "+1L);

        Customer customer = Customer.builder().id(1L).username("test").password("123456").build();
        when(this.customerRepository.findByUsername(customer.getUsername())).thenReturn(Optional.empty());

        when(this.customerRepository.save(customer)).thenReturn(customer);
        PayloadResponse payloadResponse1 = customerService.registerCustomer(customer);

        assertEquals(payloadResponse1.getMessage(), payloadResponse.getMessage());
    }

    @Test
    void registerCustomerAlreadyExists() {
        Customer customer = Customer.builder().username("test").password("123456").build();
        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setMessage("Customer already exists with username : "+customer.getUsername());

        Customer customerInfo= Customer.builder().id(12L).username("test").password("123456").build();
        when(this.customerRepository.findByUsername(customer.getUsername())).thenReturn(Optional.of(customerInfo));

        PayloadResponse payloadResponse1 = customerService.registerCustomer(customer);

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
