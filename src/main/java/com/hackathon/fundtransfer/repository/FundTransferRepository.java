package com.hackathon.fundtransfer.repository;

import com.hackathon.fundtransfer.entity.FundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundTransferRepository extends JpaRepository<FundTransfer, Long> {
    List<FundTransfer> findByFromAccountOrToAccount(String fromAccount, String toAccount);
}
