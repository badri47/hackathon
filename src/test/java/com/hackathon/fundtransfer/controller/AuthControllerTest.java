package com.hackathon.fundtransfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.fundtransfer.dtos.AuthRequest;
import com.hackathon.fundtransfer.dtos.CustomerRequest;
import com.hackathon.fundtransfer.dtos.LoginResponse;
import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.service.CustomerService;
import com.hackathon.fundtransfer.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    @BeforeEach
    public void setUp() {

        UserDetails userDetails = User.withUsername("testuser").password("123456").authorities(Collections.emptyList()).build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        String token = jwtUtil.generateToken(userDetails.getUsername());
        when(this.jwtUtil.generateToken("testuser")).thenReturn(token);
    }

    @Test
    public void registerCustomerSuccess() throws Exception {

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setUsername("test");
        customerRequest.setPassword("123456");
        customerRequest.setFirstName("test");
        customerRequest.setLastName("test");
        customerRequest.setEmail("test@gmail.com");
        customerRequest.setPhoneNumber("7894561230");
        customerRequest.setAccountNumber("1119058");
        customerRequest.setAccountType("SAVINGS");
        customerRequest.setBalance(5000.0);

        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setMessage("Customer created successfully with customer Id : "+1L);

        when(this.customerService.registerCustomer(customerRequest)).thenReturn(payloadResponse);

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void registerCustomerFail() throws Exception {
        CustomerRequest customerRequest = new CustomerRequest("", "123456", "test", "test", "test@gmail.com", "8945612307", "", "SAVINGS", 5000.0);

        PayloadResponse payloadResponse = new PayloadResponse("Customer not found");

        when(this.customerService.registerCustomer(customerRequest)).thenReturn(payloadResponse);

        this.mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAuthenticationTokenSuccess() throws Exception {
        LoginResponse loginResponse = new LoginResponse("Logged In Successfully", "testuser", "token");
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("123456");

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(loginResponse.getMessage()));
    }

    @Test
    public void createAuthenticationTokenFail() throws Exception {
        AuthRequest authRequest = new AuthRequest("", "123456");

        Authentication authentication = new UsernamePasswordAuthenticationToken("", null, null);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void createAuthenticationBadCredentials() throws Exception {
        AuthRequest authRequest = new AuthRequest("test", "123456");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("The username or password is incorrect"));

        this.mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

}
