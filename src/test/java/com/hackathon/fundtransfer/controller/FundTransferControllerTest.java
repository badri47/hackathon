package com.hackathon.fundtransfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.dtos.TransactionResponse;
import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.entity.FundTransfer;
import com.hackathon.fundtransfer.service.FundTransferService;
import com.hackathon.fundtransfer.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FundTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FundTransferService fundTransferService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private JwtUtil jwtUtil;

    private FundTransfer fundTransfer;

    @BeforeEach
    public void setup() {
        Customer customer = Customer.builder().id(12L).username("test").build();
        Account fromAccount = Account.builder().id(1L).accountNumber("118002")
                .balance(5000.0).accountType("SAVINGS").build();
        Account toAccount = Account.builder().id(2L).accountNumber("988960")
                .balance(6000.0).accountType("SAVINGS").build();
        fundTransfer = FundTransfer.builder().transactionId(1L).fromAccount(fromAccount).toAccount(toAccount)
                .amount(500.0).comment("test test")
                .customer(customer).build();
    }

    @Test
    @WithMockUser(username = "test")
    public void transferFundsSuccess() throws Exception {
        String username = "test";

        PayloadResponse payloadResponse = new PayloadResponse();
        payloadResponse.setMessage("Transaction Successful with Transaction Id : "+1L);
        when(this.fundTransferService.transferFunds(username, fundTransfer)).thenReturn(fundTransfer);

        this.mockMvc.perform(post("/fundTransfer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(fundTransfer)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(payloadResponse.getMessage()));
    }

    @Test
    public void transferFundsForbidden() throws Exception {
        String username = "test";

        when(this.fundTransferService.transferFunds(username, fundTransfer)).thenReturn(fundTransfer);

        this.mockMvc.perform(post("/fundTransfer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(fundTransfer)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "test")
    public void transferFundsFail() throws Exception {
        String username = "test";

        fundTransfer = new FundTransfer();
        when(this.fundTransferService.transferFunds(username, fundTransfer)).thenReturn(fundTransfer);

        this.mockMvc.perform(post("/fundTransfer/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(fundTransfer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test")
    public void transactionsListSuccess() throws Exception {
        String username = "test";

        TransactionResponse transactionResponse = getTransactionResponse();
        List<TransactionResponse> transactionsList = Collections.singletonList(transactionResponse);
        String accountNumber = "213546";
        when(this.fundTransferService.getTransactionsList(username, accountNumber)).thenReturn(transactionsList);

        this.mockMvc.perform(get("/fundTransfer/transactions/"+accountNumber)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    private TransactionResponse getTransactionResponse() {
        TransactionResponse transactionResponse = new TransactionResponse();
        transactionResponse.setTransactionId(fundTransfer.getTransactionId());
        transactionResponse.setFromAccount(fundTransfer.getFromAccount().getAccountNumber());
        transactionResponse.setToAccount(fundTransfer.getToAccount().getAccountNumber());
        transactionResponse.setAmount(fundTransfer.getAmount());
        transactionResponse.setTransactionDate(fundTransfer.getTransactionDate());
        transactionResponse.setComment(fundTransfer.getComment());
        return transactionResponse;
    }

    @Test
    @WithMockUser(username = "test")
    public void transactionListDetailsNotFoundException() throws Exception {
        String username = "test";
        String accountNumber = "";
        List<TransactionResponse> transactionsList = new ArrayList<>();
        when(this.fundTransferService.getTransactionsList(username, accountNumber)).thenReturn(transactionsList);

        this.mockMvc.perform(get("/fundTransfer/transactions/"+accountNumber)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().is(500));
    }
}
