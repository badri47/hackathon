package com.hackathon.fundtransfer.service;

import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.exception.DetailsNotFoundException;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * This method is used to write the business logic for register customer
     * @param customer customer details
     * @return Payload response
     */
    public PayloadResponse registerCustomer(Customer customer) {
        var response = new PayloadResponse();
        Optional<Customer> customerInfo = customerRepository.findByUsername(customer.getUsername());
        if (customerInfo.isEmpty()) {
            String password = customer.getPassword();
            customer.setPassword(passwordEncoder.encode(password));
            Customer customer1 = customerRepository.save(customer);

            response.setMessage("Customer created successfully with customer Id : "+customer1.getId());
        } else {
            response.setMessage("Customer already exists with username : "+customer.getUsername());
        }
        return response;
    }

    /**
     * This method is override method from UserDetailsService to map the user details using username
     * @param username Customer Name
     * @return User details
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customerDetail = customerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        return new CustomerDetails(customerDetail);
    }
}
