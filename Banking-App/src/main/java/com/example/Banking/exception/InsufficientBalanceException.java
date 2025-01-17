package com.example.Banking.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InsufficientBalanceException extends RuntimeException{
    private String message;

}
