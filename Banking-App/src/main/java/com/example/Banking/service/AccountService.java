package com.example.Banking.service;

import com.example.Banking.dto.AccountDto;
import com.example.Banking.entity.TransactionLog;
import com.example.Banking.exception.AccountNotFoundException;
import com.example.Banking.model.BankCreditRequest;
import com.example.Banking.model.BankDebitRequest;
import com.example.Banking.model.ResponseStatus;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountDto createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long id);

    AccountDto deposit(Long id, Double amount);

    AccountDto withdraw(Long id, Double amount) throws AccountNotFoundException;

    List<AccountDto> getAllAccounts();

    ResponseStatus deleteAccount(Long id) throws AccountNotFoundException;


    List<TransactionLog> getAllTransactionLogs();

    List<TransactionLog> getTransactionLogsByAccountId(Long accountId) throws AccountNotFoundException;

    ResponseStatus saveDebitPurchase(BankDebitRequest request);

    ResponseStatus saveCreditPurchase(BankCreditRequest request);

}
