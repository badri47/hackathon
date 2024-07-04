package com.hackathon.fundtransfer.controller;

import com.hackathon.fundtransfer.dtos.PayloadResponse;
import com.hackathon.fundtransfer.dtos.TransactionResponse;
import com.hackathon.fundtransfer.entity.FundTransfer;
import com.hackathon.fundtransfer.exception.CustomException;
import com.hackathon.fundtransfer.service.FundTransferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/fundTransfer")
public class FundTransferController {

    private final FundTransferService fundTransferService;

    public FundTransferController(FundTransferService fundTransferService) {
        this.fundTransferService = fundTransferService;
    }

    /**
     * This API is used to transfer funds to another account
     * @param principal Logged IN User details
     * @param fundTransfer Fund Transfer Details
     * @return Payload Response
     */
    @PostMapping("/")
    public ResponseEntity<PayloadResponse> transferFunds(Principal principal,
                                                         @RequestBody @Valid FundTransfer fundTransfer) throws Exception {
        var payloadResponse = new PayloadResponse();
        String username = principal.getName();
        if (fundTransfer.getAmount() <= 0) {
            throw new CustomException("Amount should be greater than 0");
        }
        FundTransfer fundTransfer1 = fundTransferService.transferFunds(username, fundTransfer);

        payloadResponse.setMessage("Transaction Successful with Transaction Id : "+fundTransfer1.getTransactionId());
        return new ResponseEntity<>(payloadResponse, HttpStatus.OK);
    }

    /**
     * This API is used to get the transactions list based on Account Number
     * @param principal LoggedIn User Information
     * @param accountNumber Account Number
     * @return Transactions List
     */
    @GetMapping("/transactions/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> transactionsList(Principal principal, @PathVariable String accountNumber) {
        String username = principal.getName();
        return new ResponseEntity<>(fundTransferService.getTransactionsList(username, accountNumber), HttpStatus.OK);
    }
}
