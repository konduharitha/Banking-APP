package com.example.Banking.controller;


import com.example.Banking.dto.AccountDto;
import com.example.Banking.entity.TransactionLog;
import com.example.Banking.exception.AccountNotFoundException;
import com.example.Banking.model.BankCreditRequest;
import com.example.Banking.model.BankDebitRequest;
import com.example.Banking.model.ResponseStatus;
import com.example.Banking.service.AccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@Tag(name="Account APIS",description = "Add Account, withdraw, delete, getById, transcation APIS")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    //add account rest api
    @PostMapping
    public ResponseEntity<AccountDto> addAccount(@RequestBody AccountDto accountDto){
        return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.CREATED);
    }

    //get account by id rest api
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id){
        AccountDto accountDto = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    //deposit amount rest api
    @PutMapping("/deposit/{id}")
    public ResponseEntity<AccountDto> deposit(@PathVariable Long id, @RequestBody Map<String, Double> request){
        double amount = request.get("amount");
        AccountDto deposited = accountService.deposit(id, amount);

        return ResponseEntity.ok(deposited);
    }

    //withdraw amount rest api
    @PutMapping("/withdraw/{id}")
    public  ResponseEntity<AccountDto> withdraw(@PathVariable Long id, @RequestBody Map<String,Double> req) throws AccountNotFoundException {
        double amount = req.get("amount");
        AccountDto withdraw = accountService.withdraw(id, amount);
        return ResponseEntity.ok(withdraw);
    }

    //get accounts rest api
    @GetMapping
    public ResponseEntity<List<AccountDto>> getAllAccounts(){
        List<AccountDto> allAccounts = accountService.getAllAccounts();
        return ResponseEntity.ok(allAccounts);
    }

   // delete account rest api
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStatus> deleteAccount(@PathVariable Long id) throws AccountNotFoundException {
        ResponseStatus response = accountService.deleteAccount(id);
        return ResponseEntity.ok()
                .body(response);
    }
    @GetMapping("/transcation")
    public ResponseEntity<List<TransactionLog>> getAllTransactions() {
        List<TransactionLog> transactions = accountService.getAllTransactionLogs();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{accountId}")
    public ResponseEntity<List<TransactionLog>> getTransactionsByAccountId(@PathVariable Long accountId) throws AccountNotFoundException {
        List<TransactionLog> transactionLogs = accountService.getTransactionLogsByAccountId(accountId);
        return ResponseEntity.ok(transactionLogs);
    }

    @PostMapping("/debit")
    public ResponseEntity<ResponseStatus> bankDebitTranscation(@RequestBody BankDebitRequest request){
        ResponseStatus responseStatus = accountService.saveDebitPurchase(request);
        return ResponseEntity.ok(responseStatus);
    }

    @PostMapping("/credit")
    public ResponseEntity<ResponseStatus> bankCreditTranscation(@RequestBody BankCreditRequest request){
        ResponseStatus response = accountService.saveCreditPurchase(request);
        return ResponseEntity.ok(response);
    }

}
