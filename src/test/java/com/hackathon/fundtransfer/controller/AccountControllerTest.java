package com.hackathon.fundtransfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private Customer customer;

    private Account account;

    @BeforeEach
    public void setup() {
        customer = Customer.builder().id(12L).username("test").build();
        account = Account.builder().accountNumber("118852").accountType("SAVINGS").balance(100.05)
                .customer(customer).build();
    }

    @Test
    @WithMockUser(username = "test")
    public void createAccountSuccess() throws Exception {
        String username = "test";

        when(this.accountService.createAccountForCustomer(username, account)).thenReturn(account);

        this.mockMvc.perform(post("/account/")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test")
    public void createAccountFail() throws Exception {
        String username = "test";
        Customer customer = Customer.builder().id(12L).username("test").build();
        Account account = Account.builder().accountType("SAVINGS").balance(100.05)
                .customer(customer).build();

        when(this.accountService.createAccountForCustomer(username, account)).thenReturn(account);

        this.mockMvc.perform(post("/account/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAccountForbidden() throws Exception {
        String username = "";
        Customer customer = Customer.builder().id(12L).username("test").build();
        Account account = Account.builder().accountType("SAVINGS").balance(100.05)
                .customer(customer).build();

        when(this.accountService.createAccountForCustomer(username, account)).thenReturn(account);

        this.mockMvc.perform(post("/account/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test")
    public void showBalanceSuccess() throws Exception {
        String username = "test";

        List<Account> accountList = Collections.singletonList(account);
        when(this.accountService.showBalance(username)).thenReturn(accountList);

        this.mockMvc.perform(get("/account/showBalance")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void showBalanceFail() throws Exception {
        String username = "";
        List<Account> accountList = Collections.singletonList(account);
        when(this.accountService.showBalance(username)).thenReturn(accountList);

        this.mockMvc.perform(get("/account/showBalance")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
