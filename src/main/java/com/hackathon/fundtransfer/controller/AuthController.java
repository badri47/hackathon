package com.hackathon.fundtransfer.controller;

import com.hackathon.fundtransfer.dtos.AuthRequest;
import com.hackathon.fundtransfer.dtos.CustomerRequest;
import com.hackathon.fundtransfer.dtos.LoginResponse;
import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.service.CustomerService;
import com.hackathon.fundtransfer.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@Slf4j
public class AuthController {

    private final CustomerService customerService;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(CustomerService customerService, JwtUtil jwtUtil,
                          AuthenticationManager authenticationManager) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * This API is used to register the customer
     * @param customerRequest customer details
     * @return Response
     */
    @PostMapping("/register")
    public ResponseEntity<PayloadResponse> registerCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return new ResponseEntity<>(customerService.registerCustomer(customerRequest), HttpStatus.CREATED);
    }

    /**
     * This API is used to log In customer and generate token
     * @param authRequest Auth request
     * @return Login Response
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> createAuthenticationToken(@Valid @RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        var loginResponse = new LoginResponse();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(authRequest.getUsername());

            loginResponse.setMessage("Logged In Successfully");
            loginResponse.setToken(token);
            loginResponse.setUsername(userDetails.getUsername());
        }
        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }
}
