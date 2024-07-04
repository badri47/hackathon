package com.hackathon.fundtransfer.service;

import com.hackathon.fundtransfer.dtos.TransactionResponse;
import com.hackathon.fundtransfer.entity.Account;
import com.hackathon.fundtransfer.entity.Customer;
import com.hackathon.fundtransfer.entity.FundTransfer;
import com.hackathon.fundtransfer.exception.CustomException;
import com.hackathon.fundtransfer.exception.DetailsNotFoundException;
import com.hackathon.fundtransfer.exception.UnAuthorizedException;
import com.hackathon.fundtransfer.repository.AccountRepository;
import com.hackathon.fundtransfer.repository.CustomerRepository;
import com.hackathon.fundtransfer.repository.FundTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FundTransferService {

    private final FundTransferRepository fundTransferRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public FundTransferService(FundTransferRepository fundTransferRepository, AccountRepository accountRepository,
                               CustomerRepository customerRepository) {
        this.fundTransferRepository = fundTransferRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * This method is used to transfer funds from account to another account
     * @param username Customer Name
     * @param fundTransfer Funds Transfer Details
     * @return Funds Transfer details with transactionId
     * @throws Exception Exception details
     */
    @Transactional
    public FundTransfer transferFunds(String username, FundTransfer fundTransfer) throws Exception {

        Account fromAccount = accountRepository.findByAccountNumber(fundTransfer.getFromAccount().getAccountNumber())
                .orElseThrow(() -> new Exception("From Account Not Found"));
        Account toAccount = accountRepository.findByAccountNumber(fundTransfer.getToAccount().getAccountNumber())
                .orElseThrow(() -> new Exception("To Account Not Found"));

        if (!fromAccount.getCustomer().getUsername().equals(username)) {
            throw new UnAuthorizedException("UnAuthorized Transaction.");
        }

        if (fromAccount.getBalance() < fundTransfer.getAmount()) {
            throw new CustomException("Insufficient Funds to Transfer");
        }

        fromAccount.setBalance(fromAccount.getBalance() - fundTransfer.getAmount());
        toAccount.setBalance(toAccount.getBalance() + fundTransfer.getAmount());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        fundTransfer.setLocalDateTime(LocalDateTime.now());

        return fundTransferRepository.save(fundTransfer);
    }


    /**
     * This method is used to fetch the Transactions list for the account number
     * @param username Customer Name
     * @param accountNumber Account Number
     * @return Transactions List
     */
    public List<TransactionResponse> getTransactionsList(String username, String accountNumber) {
        List<TransactionResponse> transactionsList = new ArrayList<>();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("Customer Not Found"));
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new DetailsNotFoundException("Account Details not Found"));
        if (account.getCustomer().getId().equals(customer.getId())) {
            List<FundTransfer> fundTransferList = fundTransferRepository.
                    findByFromAccountIdOrToAccountId(account.getId(), account.getId());
            if (!fundTransferList.isEmpty()) {
                fundTransferList.forEach(fundTransfer -> {
                    var transactionResponse = new TransactionResponse();
                    transactionResponse.setTransactionId(fundTransfer.getTransactionId());
                    transactionResponse.setFromAccount(fundTransfer.getFromAccount().getAccountNumber());
                    transactionResponse.setToAccount(fundTransfer.getToAccount().getAccountNumber());
                    transactionResponse.setAmount(fundTransfer.getAmount());
                    transactionResponse.setComment(fundTransfer.getComment());
                    transactionResponse.setTransactionDate(fundTransfer.getLocalDateTime());
                    transactionsList.add(transactionResponse);
                });
            }
        } else {
            throw new UnAuthorizedException("UnAuthorized Request");
        }
        return transactionsList;
    }
}
