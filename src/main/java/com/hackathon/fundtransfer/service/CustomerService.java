package com.hackathon.fundtransfer.service;

import com.hackathon.fundtransfer.dtos.CustomerRequest;
import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.exception.CustomException;
import com.hackathon.fundtransfer.repository.AccountRepository;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * This method is used to write the business logic for register customer
     * @param customerRequest customer details
     * @return Payload response
     */
    @Transactional
    public PayloadResponse registerCustomer(CustomerRequest customerRequest) {
        var response = new PayloadResponse();
        Optional<Customer> customerInfo = customerRepository.findByUsername(customerRequest.getUsername());
        if (customerInfo.isEmpty()) {
            Customer customer = new Customer();
            customer.setUsername(customerRequest.getUsername());
            customer.setPassword(passwordEncoder.encode(customerRequest.getPassword()));
            customer.setFirstName(customerRequest.getFirstName());
            customer.setLastName(customerRequest.getLastName());
            customer.setEmail(customerRequest.getEmail());
            customer.setPhoneNumber(customerRequest.getPhoneNumber());

            Customer customer1 = customerRepository.save(customer);

            Optional<Account> checkAccountExist = accountRepository.findByAccountNumber(customerRequest.getAccountNumber());
            if (checkAccountExist.isPresent()) {
                throw new CustomException("Account Number already exists");
            }

            Account account = new Account();
            account.setCustomer(customer1);
            account.setAccountNumber(customerRequest.getAccountNumber());
            account.setAccountType(customerRequest.getAccountType());
            account.setBalance(customerRequest.getBalance());

            accountRepository.save(account);

            response.setMessage("Customer created successfully with customer Id : "+customer1.getId());
        } else {
            response.setMessage("Customer already exists with username : "+customerRequest.getUsername());
        }
        return response;
    }

    /**
     * This method is override method from UserDetailsService to map the user details using username
     * @param username Customer Name
     * @return User details
     * @throws UsernameNotFoundException UserName Not Found Exception
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customerDetail = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        return new CustomerDetails(customerDetail);
    }
}
