package com.example.Banking.mapper;

import com.example.Banking.dto.AccountDto;
import com.example.Banking.entity.Account;

import java.util.ArrayList;

public class AccountMapper {

    public static Account mapToAccount(AccountDto accountDto){
        Account account1 = new Account(
                accountDto.getId(),
                accountDto.getAccountHolderName(),
                accountDto.getBalance(),
                new ArrayList<>()

        );
        return account1;
    }

    public static AccountDto mapToAccountDto(Account account){
        AccountDto accountDto = new AccountDto(
                account.getId(),
                account.getAccountHolderName(),
                account.getBalance()
        );
        return accountDto;
    }
}
