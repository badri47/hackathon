package com.hackathon.fundtransfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.entity.FundTransfer;
import com.hackathon.fundtransfer.service.FundTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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
    private FundTransfer fundTransfer;

    @BeforeEach
    public void setup() {
        Customer customer = Customer.builder().id(12L).username("test").build();
        fundTransfer = FundTransfer.builder().transactionId(1L).fromAccount("118002").toAccount("988960").amount(500.0)
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

        List<FundTransfer> transactionsList = Collections.singletonList(fundTransfer);
        String accountNumber = "213546";
        when(this.fundTransferService.getTransactionsList(username, accountNumber)).thenReturn(transactionsList);

        this.mockMvc.perform(get("/fundTransfer/transactions/"+accountNumber)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}
