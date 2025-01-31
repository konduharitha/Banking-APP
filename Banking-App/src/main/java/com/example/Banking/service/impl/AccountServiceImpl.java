package com.example.Banking.service.impl;

import com.example.Banking.dto.AccountDto;
import com.example.Banking.entity.Account;
import com.example.Banking.entity.TransactionLog;
import com.example.Banking.exception.AccountNotFoundException;
import com.example.Banking.exception.InsufficientBalanceException;
import com.example.Banking.mapper.AccountMapper;
import com.example.Banking.model.BankCreditRequest;
import com.example.Banking.model.BankDebitRequest;
import com.example.Banking.model.ResponseStatus;
import com.example.Banking.repository.AccountRepository;
import com.example.Banking.repository.TransactionLogRepository;
import com.example.Banking.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final TransactionLogRepository transactionLogRepository;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionLogRepository transactionLogRepository){
        this.accountRepository = accountRepository;
        this.transactionLogRepository = transactionLogRepository;
    }
    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account1 = AccountMapper.mapToAccount(accountDto);
        Account saved = accountRepository.save(account1);
        return AccountMapper.mapToAccountDto(saved);

    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Account doesn't found"));

        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, Double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Account doesn't found"));
        double total = account.getBalance() + amount;

        account.setBalance(total);

        Account saved = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(saved);
    }

    @Override
    public AccountDto withdraw(Long id, Double amount) throws AccountNotFoundException {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account doesn't exist"));

        if(account.getBalance() < amount){
            throw new InsufficientBalanceException("user does not have sufficient balance");
        }else {

            double total = account.getBalance() - amount;
            account.setBalance(total);
            TransactionLog transactionLog = new TransactionLog();
            transactionLog.setTransactionDate(LocalDateTime.now());
            transactionLog.setDescription("user debit amount: " + amount + ", " + "current amount : " +total);
            account.getTransactionLogs().add(transactionLog);
            Account saved = accountRepository.save(account);
            return AccountMapper.mapToAccountDto(saved);
        }

    }

    @Override
    public List<AccountDto> getAllAccounts() {
       List<Account> accounts = accountRepository.findAll();

        return accounts.stream()
                .map((AccountMapper::mapToAccountDto))
                .toList();
    }

    @Override
    public ResponseStatus deleteAccount(Long id) throws AccountNotFoundException {
        if (!accountRepository.existsById(id)) {
            throw new AccountNotFoundException("Account doesn't exist");
        }
        accountRepository.deleteById(id);

        return ResponseStatus.builder()
                .isSuccess(Boolean.TRUE)
                .message("Successfully deleted user by id : " + id)
                .build();

    }

    @Override
    public List<TransactionLog> getAllTransactionLogs() {
        return transactionLogRepository.findAll();
    }
    // Fetch transactions for a particular user
    public List<TransactionLog> getTransactionLogsByAccountId(Long accountId) throws AccountNotFoundException {
        // Ensure the account exists
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        // Return the transactions associated with the account
        return account.getTransactionLogs();
    }

    @Override
    @Transactional
    public ResponseStatus saveDebitPurchase(BankDebitRequest request) {

        //1. find the user
        //2. check if he has enough balance.
        //3. deduct the money and save the user amount.
        //4. save the new statement in statement table.
        //5. return the response.



        Optional<Account> userDetails = accountRepository.findById(request.getUserId());

        if(userDetails.isPresent()){

            Account userAccount = userDetails.get();

            log.info(String.valueOf(request.getUserId()));

            if(userAccount.getBalance() >= request.getDebitAmount()){
                Double newBalance = userAccount.getBalance() - request.getDebitAmount();
                userAccount.setBalance(newBalance);

                TransactionLog transactionLog = TransactionLog.builder()
                        .debitAmount(request.getDebitAmount())
                        .description(request.getTransactionDetails())
                        .transactionDate(LocalDateTime.now())
                        .build();

                userAccount.getTransactionLogs().add(transactionLog);

               accountRepository.save(userAccount);

                return ResponseStatus
                        .builder()
                        .isSuccess(Boolean.TRUE)
                        .message("Transaction Success.")
                        .detailMessage("Successfully deducated the amount. ")
                        .build();

            }else{
                return ResponseStatus
                        .builder()
                        .isSuccess(Boolean.FALSE)
                        .message("Transaction Failed")
                        .detailMessage("Due to Insufficient funds")
                        .build();
            }
        }else{
            return ResponseStatus
                    .builder()
                    .isSuccess(Boolean.FALSE)
                    .message("Transaction Failed.")
                    .detailMessage("User Not Found")
                    .build();
        }

    }

    @Override
    public ResponseStatus saveCreditPurchase(BankCreditRequest request) {


        Optional<Account> userDetails = accountRepository.findById(request.getUserId());

        if (userDetails.isPresent()) {

            Account userAccount = userDetails.get();

            Double newBalance = userAccount.getBalance() + request.getCreditAmount();
            userAccount.setBalance(newBalance);

            TransactionLog transactionLog = TransactionLog.builder()
                    .creditAmount(request.getCreditAmount())
                    .description(request.getTransactionDetails())
                    .transactionDate(LocalDateTime.now())
                    .build();

            userAccount.getTransactionLogs().add(transactionLog);

            accountRepository.save(userAccount);

            return ResponseStatus
                    .builder()
                    .isSuccess(Boolean.TRUE)
                    .message("Transaction Success.")
                    .detailMessage("Successfully credited")
                    .build();


        } else {
            return ResponseStatus
                    .builder()
                    .isSuccess(Boolean.FALSE)
                    .message("Transaction Failed.")
                    .detailMessage("User Not Found")
                    .build();
        }

    }

}


